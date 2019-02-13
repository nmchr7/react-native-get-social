package com.rngetsocial;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import im.getsocial.sdk.GetSocial;

public class RNGetSocialModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNGetSocialModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void onInitialized(final Callback callback) {
        GetSocial.whenInitialized(new Runnable() {
            @Override
            public void run() {
                callback.invoke();
            }
        });
    }

    @ReactMethod
    public boolean isInitialized() {
        return GetSocial.isInitialized();
    }

    @ReactMethod
    public void showInviteUI(final Promise promise) {
        // TODO implement
    }


    @Override
    public String getName() {
        return "RNGetSocial";
    }
}