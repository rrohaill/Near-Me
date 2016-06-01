package com.rohail.apps.nearme.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceConnector {
    public static final String PREF_NAME = "NearMePrefrences";
    public static final int MODE = Context.MODE_PRIVATE;

    public static final String CURRENT_BALANCE = "CURRENT_BALANCE";
    public static final String AVAILABLE_CREDIT = "AVAILABLE_CREDIT";
    public static final String SEQ_ID = "SEQ_ID";
    public static final String USER_ID = "USER_ID";

    public static final String VERSION_USAGE_LEVEL = "VERSION_USAGE_LEVEL";
    public static final String APP_VERSION = "APP_VERSION";
    public static final String APP_BLOCK_MSG = "APP_BLOCK_MESSAGE";
    public static final String CATALOG_VERSION = "Catalog_verison";
    public static final String CATALOG_DATA = "cat_data";
    public static final String TUTORIAL = "show_tutorial";
    public static final String FAQ_VERSION = "faq_version";
    public static final String FAQS_DATA = "faqs_data";
    public static final String FCM_TOKEN = "fcm_token";

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key,
                                      boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();
    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
}
