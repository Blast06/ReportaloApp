package com.example.juliod07_laptop.firebasecurso;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by JulioD07-LAPTOP on 8/29/2017.
 */

public class FacebookApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }
}
