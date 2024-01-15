package com.oshrink.devicedoctor;

import android.app.Activity;
import android.os.Build;
import android.view.Window;

import androidx.annotation.ColorRes;

public class AppTimeBar {
    public static void setStatusBarColor(Activity activity, @ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        }
    }
}
