package com.stateless.app.richeditordemo;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Administrator on 2016/12/19.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
