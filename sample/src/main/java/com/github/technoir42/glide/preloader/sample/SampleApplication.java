package com.github.technoir42.glide.preloader.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

public class SampleApplication extends Application {
    @Override
    @SuppressLint("VisibleForTests")
    public void onCreate() {
        super.onCreate();
        Glide.init(this, new GlideBuilder().setLogLevel(Log.DEBUG));
    }
}
