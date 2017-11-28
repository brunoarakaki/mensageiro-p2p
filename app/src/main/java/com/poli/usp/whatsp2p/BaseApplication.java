package com.poli.usp.whatsp2p;

import android.app.Application;
import android.content.Context;

import poli.com.mobile2you.whatsp2p.BuildConfig;

import com.poli.usp.whatsp2p.data.local.PreferencesHelper;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mobile2you on 18/08/16.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Fabric
        if(BuildConfig.REPORT_CRASH){
            Fabric.with(this, new Crashlytics());
        }

        PreferencesHelper.initializeInstance(getApplicationContext());
    }

    public static Context getContext() {
       return getContext();
    }
}
