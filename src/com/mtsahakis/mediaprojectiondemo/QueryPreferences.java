package com.mtsahakis.mediaprojectiondemo;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String sIsFirstRun = "IS_FIRST_RUN";

    public static boolean isFirstRun(Context context) {
        boolean result = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(sIsFirstRun, true);
        if(result) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(sIsFirstRun, false).apply();
        }
        return result;
    }

}
