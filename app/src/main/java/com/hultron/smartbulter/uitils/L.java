package com.hultron.smartbulter.uitils;

import android.util.Log;

/**
 * 自定义 Log
 */

public class L {
    //开关
    public static final boolean DEBUG = true;
    //TAG
    private static final String TAG = "Smartbutler";

    //五个等级 DIWEF
    public static void d(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }

    public static void i(String text) {
        if (DEBUG) {
            Log.i(TAG, text);
        }
    }

    public static void w(String text) {
        if (DEBUG) {
            Log.w(TAG, text);
        }
    }

    public static void e(String text) {
        if (DEBUG) {
            Log.e(TAG, text);
        }
    }

}
