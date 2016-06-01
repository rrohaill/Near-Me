package com.rohail.apps.nearme.utilities;

import android.util.Log;

public class Logger {

    private static final String TAG = "Near Me: ";

    // -display all logs only when isDebuggingMode = true
    private static boolean isDebuggingMode = true;

    public static void setDebuggingMode(boolean isDebuggingMode) {
        Logger.isDebuggingMode = isDebuggingMode;
    }

    public static void e(String msg) {
        if (isDebuggingMode)
            Log.e(TAG, msg);
    }

    public static void e(Exception e) {
        if (isDebuggingMode) {
            try {
                Log.e(TAG, e.getMessage());
            } catch (Exception e1) {
                try {
                    Log.e(TAG,
                            "Some Exception occured and unable to print Exception Message");
                    Log.e(TAG, "Cause = " + e1.getStackTrace());
                } catch (Exception e2) {
                    Log.e(TAG,
                            "Some Exception occured and unable to print Exception Cause");
                }
            }
        }
    }

    public static void i(String msg) {
        if (isDebuggingMode)
            Log.i(TAG, msg);
    }
}