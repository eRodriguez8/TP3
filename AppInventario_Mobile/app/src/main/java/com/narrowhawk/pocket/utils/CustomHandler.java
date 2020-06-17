package com.narrowhawk.pocket.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.narrowhawk.pocket.activities.MainActivity;

public class CustomHandler implements Thread.UncaughtExceptionHandler {

    Activity activity;

    public CustomHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        Log.println(1, "Error", ex.getMessage());
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("hasCrashed", true);
        activity.startActivity(intent);
        activity.finish();

        System.exit(0);
    }

}
