package com.crab.es.study;

import android.app.Application;

public class MyApplication extends Application {
    private static Application sApplicationInstance;

    public static Application getApplication() {
        return sApplicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationInstance = this;
    }
}