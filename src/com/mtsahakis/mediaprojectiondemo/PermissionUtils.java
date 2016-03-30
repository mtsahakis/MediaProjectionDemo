package com.mtsahakis.mediaprojectiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {

    public static boolean useRunTimePermissions() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean hasPermission(Activity activity, String permission) {
        if (useRunTimePermissions()) {
            return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static boolean shouldShowRational(Activity activity, String permission) {
        if (useRunTimePermissions()) {
            return !hasPermission(activity, permission)
                    && activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    public static boolean canWriteExternalStorage(Activity activity) {
        return hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean shouldShowRationalWriteExternalStorage(Activity activity) {
        return shouldShowRational(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
