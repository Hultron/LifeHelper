package com.hultron.smartbulter.application;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.hultron.smartbulter.uitils.StaticClass;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;


public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        //初始化 Bugly
        CrashReport.initCrashReport(getApplicationContext(), StaticClass.BUGLY_APP_ID, true);

        //初始化 Bmob
        Bmob.initialize(this, StaticClass.BMOB_APP_ID);

        //初始化语音识别
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" +
                StaticClass.VOICE_KEY);

        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
    }

    public static Context getContext() {
        return mContext;
    }
}
