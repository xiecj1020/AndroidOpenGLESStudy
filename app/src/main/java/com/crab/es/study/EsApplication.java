package com.crab.es.study;

import android.app.Application;
import android.content.res.Resources;

public class EsApplication extends Application{
    //临时取代方案
    private static Resources sResource;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static Resources getGlobalResource(){
        return sResource;
    }
    public static void setGlobalRecource(Resources resources){
        sResource = resources;
    }
}
