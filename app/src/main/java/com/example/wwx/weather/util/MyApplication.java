package com.example.wwx.weather.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by wwx on 2018/2/26.
 * 自定义application
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
    }

    public static Context getContext() {
        return context;
    }
}
