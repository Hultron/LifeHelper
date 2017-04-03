package com.hultron.lifehelper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.hultron.lifehelper.gson.weather.Weather;
import com.hultron.lifehelper.uitils.HttpUtil;
import com.hultron.lifehelper.uitils.ParsingJson;
import com.hultron.lifehelper.uitils.ShareUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 后台自动更新天气
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000; //8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    * 更新天气信息
    * */
    private void updateWeather() {
        String weatherString = ShareUtils.getString(this, "weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = ParsingJson.handleWeatherResponse(weatherString);
            if (weather != null) {
                final String weatherId = weather.basic.weatherId;

                String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                        "&key=edcc301174514a5e811431c763457862";
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather weather1 = ParsingJson.handleWeatherResponse(responseText);
                        if (weather1 != null && "ok".equals(weather1.status)) {
                            ShareUtils.putString(AutoUpdateService.this, "weather",
                                    responseText);
                        }
                    }
                });
            }
        }
    }

    /*
    * 更新必应每日一图
    * */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                ShareUtils.putString(AutoUpdateService.this, "bing_pic", bingPic);
            }
        });
    }
}
