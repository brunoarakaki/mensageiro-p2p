package br.com.mobile2you.m2ybase;

import android.app.Application;
import android.content.Context;

import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
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
