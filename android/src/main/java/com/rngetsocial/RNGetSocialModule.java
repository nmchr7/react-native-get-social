package com.rngetsocial;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import im.getsocial.sdk.GetSocial;
import im.getsocial.sdk.ui.GetSocialUi;
import im.getsocial.sdk.usermanagement.OnUserChangedListener;
import im.getsocial.sdk.ui.invites.InvitesViewBuilder;
import im.getsocial.sdk.invites.FetchReferralDataCallback;
import im.getsocial.sdk.invites.ReferralData;
import im.getsocial.sdk.invites.LinkParams;
import im.getsocial.sdk.invites.InviteContent;
import im.getsocial.sdk.ui.invites.InviteUiCallback;
import im.getsocial.sdk.GetSocialException;
import im.getsocial.sdk.media.MediaAttachment;

import java.util.Map;
import java.util.HashMap;

import android.support.annotation.Nullable;
import android.util.Log;

public class RNGetSocialModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNGetSocialModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        // most of the data in getSocial is cachable, so setup listeners here
        // then send the response to JS
        setupGetSocial();
    }

    @ReactMethod
    public void showInviteUI(final ReadableMap params, final ReadableMap config, final Promise promise) {

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                InvitesViewBuilder uiBuilder = GetSocialUi.createInvitesView();

                LinkParams linkParams = new LinkParams();

                //
                // convert the params to link params
                //
                for (Map.Entry<String, Object> entry : params.toHashMap().entrySet()) {

                    // since linkParams can only be strings,
                    // make sure to notify the React native thread in case it send anything else
                    // as using .toString() would convert an id sent as integer from 3 to 3.0 (which
                    // is obviously not correct)
                    if (!(entry.getValue() instanceof String)) {
                        promise.reject(entry.getKey() + " is not a string");
                        return;
                    }
                    linkParams.put(entry.getKey(), entry.getValue().toString());
                }

                uiBuilder.setLinkParams(linkParams);

                //
                // create the invite content (social media config)
                //
                InviteContent.Builder contentBuilder = InviteContent.createBuilder();

                if (config.hasKey("subject")) {

                    contentBuilder.withSubject(config.getString("subject"));
                }

                if (config.hasKey("text")) {

                    contentBuilder.withText(config.getString("text"));
                }

                if (config.hasKey("imageUrl")) {

                    contentBuilder.withMediaAttachment(MediaAttachment.imageUrl(config.getString("imageUrl")));
                }

                uiBuilder.setCustomInviteContent(contentBuilder.build());

                //
                // change the window title
                //
                if (config.hasKey("windowTitle")) {

                    uiBuilder.setWindowTitle(config.getString("windowTitle"));
                }

                //
                // show the UI
                //
                boolean wasShown = uiBuilder.setInviteCallback(new InviteUiCallback() {
                    @Override
                    public void onComplete(String channelId) {
                        promise.resolve(null);
                    }

                    @Override
                    public void onCancel(String channelId) {
                        promise.reject("Canceled"); // TODO: we should probably add an object {isCanceled: true}
                    }

                    @Override
                    public void onError(String channelId, Throwable throwable) {
                        promise.reject(throwable);
                    }
                }).show();

                Log.i("GetSocial", "GetSocial Smart Invites UI was shown: " + wasShown);
            }
        });
    }

    @Override
    public String getName() {
        return "RNGetSocial";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        // add the LinkParams constants
        constants.put("KEY_CUSTOM_TITLE", LinkParams.KEY_CUSTOM_TITLE);
        constants.put("KEY_CUSTOM_DESCRIPTION", LinkParams.KEY_CUSTOM_DESCRIPTION);
        constants.put("KEY_CUSTOM_IMAGE", LinkParams.KEY_CUSTOM_IMAGE);
        constants.put("KEY_CUSTOM_YOUTUBE_VIDEO", LinkParams.KEY_CUSTOM_YOUTUBE_VIDEO);

        // use "constants" on the module to account for the scenario where the events
        // have been issued
        // before the js has been loaded
        constants.put("initialized", GetSocial.isInitialized());
        constants.put("userId", GetSocial.isInitialized() ? GetSocial.User.getId() : ""); // use the empty string
                                                                                          // instead of null as on iOS
                                                                                          // you can't put null in a
                                                                                          // dictionary so we want to
                                                                                          // have consistency

        return constants;
    }

    @ReactMethod
    public void whenInitialized(final Promise promise) {
        GetSocial.whenInitialized(new Runnable() {
            @Override
            public void run() {

                // finalize the promise
                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void getReferralData(final Promise promise) {
        GetSocial.getReferralData(new FetchReferralDataCallback() {
            @Override
            public void onSuccess(@Nullable ReferralData referralData) {
                if (referralData != null) {

                    Map<String, String> data = referralData.getReferralLinkParams();
                    WritableMap params = Arguments.createMap();

                    // convert the params to a form understandable by the Bridge
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        params.putString(entry.getKey(), entry.getValue());
                    }

                    // send the params to JS
                    promise.resolve(params);
                } else {

                    // no referral data found, but still resolve
                    promise.resolve(null);
                }
            }

            @Override
            public void onFailure(GetSocialException error) {

                // something went wrong
                promise.reject(error);
            }
        });
    }

    // Thanks to
    // https://github.com/getsocial-im/getsocial-android-sdk/blob/master/example/src/main/java/im/getsocial/demo/MainActivity.java#L232
    protected void setupGetSocial() {

        GetSocial.whenInitialized(new Runnable() {

            @Override
            public void run() {

                // notify the JS thread that the GetSocial SDK has initialized
                // so it can be cached there
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("getSocialInitialized", null);
            }
        });

        GetSocial.User.setOnUserChangedListener(new OnUserChangedListener() {

            @Override
            public void onUserChanged() {

                WritableMap args = Arguments.createMap();
                args.putString("userId", GetSocial.User.getId());

                // notify the JS thread that the GetSocial user has changed
                // so it can be cached there
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("getSocialUserChanged", args);
            }
        });
    }
}