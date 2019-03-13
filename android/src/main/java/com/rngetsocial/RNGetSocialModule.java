package com.rngetsocial;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import im.getsocial.sdk.GetSocial;
import im.getsocial.sdk.usermanagement.OnUserChangedListener;
import im.getsocial.sdk.invites.FetchReferralDataCallback;
import im.getsocial.sdk.invites.ReferralData;
import im.getsocial.sdk.GetSocialException;

import java.util.Map;
import java.util.HashMap;

import android.support.annotation.Nullable;

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
    public void showInviteUI(final Promise promise) {
        // TODO implement
    }

    @Override
    public String getName() {
        return "RNGetSocial";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        // use "constants" on the module to account for the scenario where the events
        // have been issued
        // before the js has been loaded
        constants.put("initialized", GetSocial.isInitialized());
        constants.put("userId", GetSocial.User.getId());
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