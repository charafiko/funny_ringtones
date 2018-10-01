package com.acc.gp.ringtones.funny.application;

import android.support.multidex.MultiDexApplication;

import com.acc.gp.ringtones.funny.utils.Const;
import com.google.android.gms.ads.MobileAds;

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, Const.KEY_AD_MOB);
//        Crashlytics crashlytics = new Crashlytics.Builder()
//                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
//                .build();
//        Fabric.with(this, crashlytics);
    }
}
