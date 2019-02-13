package com.rngetsocial;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import im.getsocial.sdk.GetSocial;
import im.getsocial.sdk.invites.InviteCallback;
import im.getsocial.sdk.ui.GetSocialUi;

public class RNGetSocialModule extends ReactContextBaseJavaModule {

    private Promise mPickerPromise;
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
    public void sendInvite(String channelId, final Callback successCallback, final Callback failureCallback) {
        GetSocial.sendInvite(channelId, new InviteCallback() {
            @Override
            public void onComplete() {
                successCallback.invoke();
            }

            @Override
            public void onCancel() {
                failureCallback.invoke("canceled");
            }

            @Override
            public void onError(Throwable throwable) {
                failureCallback.invoke(throwable.getMessage());
            }
        });
    }


    @Override
    public String getName() {
        return "RNGetSocial";
    }
}