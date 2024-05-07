package com.example.moreprati;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class AppSettings extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);

    }
}
