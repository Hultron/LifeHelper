package com.hultron.lifehelper.uitils;

/*
* SharedPreferences封装
* */

import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtils {

    public static final String NAME = "config";

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context
                .MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context
                .MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context
                .MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    //删除单个数据
    public static void deleShare(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context
                .MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    //删除全部数据
    public static void deleAll(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context
                .MODE_PRIVATE).edit();
        editor.clear().apply();
    }
}
