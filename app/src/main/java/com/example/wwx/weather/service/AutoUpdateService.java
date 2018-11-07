package com.example.wwx.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.wwx.weather.gson.Weather;
import com.example.wwx.weather.util.HttpUtil;
import com.example.wwx.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wwx on 2018/11/7.
 * 更新服务
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
        int hour = 8 * 60 * 60 * 1000;
        //SystemClock.elapsedRealtime()计算某个时间经历了多长时间有意义
        long tiggerTime = SystemClock.elapsedRealtime() + hour;
        Intent intent1 = new Intent();
        PendingIntent pendingIntent = PendingIntent.
                getService(this, 0, intent1, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,tiggerTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                    "&key=a819414e5f164af28020aec722ee174d";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseTxt = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(responseTxt);
                    if (weather1 != null && "ok".equals(weather1.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(
                                        AutoUpdateService.this).edit();
                        editor.putString("weather", responseTxt);
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
