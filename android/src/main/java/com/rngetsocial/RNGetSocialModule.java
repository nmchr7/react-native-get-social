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

import java.util.Map;
import java.util.HashMap;

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
    public void onInitialized(final Callback callback) {
        GetSocial.whenInitialized(new Runnable() {
            @Override
            public void run() {

                // notify the JS callback
                callback.invoke();
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