package com.example.wwx.weather.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wwx.weather.R;
import com.example.wwx.weather.gson.ForeCast;
import com.example.wwx.weather.gson.Weather;
import com.example.wwx.weather.service.AutoUpdateService;
import com.example.wwx.weather.util.HttpUtil;
import com.example.wwx.weather.util.ToastUtil;
import com.example.wwx.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wwx on 2018/11/7.
 * 天气界面
 */

public class WeatherActivity extends AppCompatActivity {

    private ImageView imgPic;
    private ScrollView scrollWeather;
    private TextView txtTitleCity, txtTitleUpdateTime;
    private TextView txtNowDegree, txtNowWeatherInfo;
    private LinearLayout linearForecast;
    private TextView txtAqi, txtPm25;
    private TextView txtSuggestComfort, txtSuggestCarWash, txtSuggestSport;

    public SwipeRefreshLayout refreshLayout;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        initListener();
    }

    private void initListener() {
        imgPic = findViewById(R.id.img_pic);
        scrollWeather = findViewById(R.id.scroll_weather);
        txtTitleCity = findViewById(R.id.txt_title_city);
        txtTitleUpdateTime = findViewById(R.id.txt_title_update_time);
        txtNowDegree = findViewById(R.id.txt_degree);
        txtNowWeatherInfo = findViewById(R.id.txt_weather_info);
        linearForecast = findViewById(R.id.linear_forecast);
        txtAqi = findViewById(R.id.aqi_txt);
        txtPm25 = findViewById(R.id.pm25_txt);
        txtSuggestComfort = findViewById(R.id.comfort_txt);
        txtSuggestCarWash = findViewById(R.id.car_wash_txt);
        txtSuggestSport = findViewById(R.id.sport_txt);
        refreshLayout = findViewById(R.id.swipe_refresh);

        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        Button btnHome = findViewById(R.id.btn_change_city);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        String weatherId = null;
        if (weatherString != null) {
            /*
                有缓存时直接解析天气数据
             */
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weather != null) {
                weatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            }
        } else {
            /*
                无缓存时服务器查询天气数据
             */
            weatherId = getIntent().getStringExtra("weather_id");
            scrollWeather.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        final String finalWeatherId = weatherId;
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(finalWeatherId);
            }
        });

        String bingPic = sp.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(imgPic);
        } else {
            loadBingPic();
        }
    }

    /**
     * 处理并展示weather实体类数据
     */
    private void showWeatherInfo(Weather weather) {

        txtTitleCity.setText(weather.basic.cityName);
        txtTitleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        String degree = weather.now.temperature + "℃";
        txtNowDegree.setText(degree);
        txtNowWeatherInfo.setText(weather.now.nowCond.nowCondTxt);
        linearForecast.removeAllViews();
        for (ForeCast foreCast : weather.foreCastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    linearForecast, false);
            TextView txtDate = view.findViewById(R.id.date_txt);
            TextView txtInfo = view.findViewById(R.id.info_txt);
            TextView txtMax = view.findViewById(R.id.max_txt);
            TextView txtMin = view.findViewById(R.id.min_txt);
            txtDate.setText(foreCast.date);
            txtInfo.setText(foreCast.foreCastCond.foreCastTxt);
            txtMax.setText(foreCast.temperature.temperatureMax);
            txtMin.setText(foreCast.temperature.temperatureMin);
            linearForecast.addView(view);
        }
        if (weather.aqi != null) {
            txtAqi.setText(weather.aqi.aqiCity.aqi);
            txtPm25.setText(weather.aqi.aqiCity.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.comfortTxt;
        txtSuggestComfort.setText(comfort);
        String carWash = "洗车指数：" + weather.suggestion.carWash.carWashTxt;
        txtSuggestCarWash.setText(carWash);
        String sport = "运动建议：" + weather.suggestion.sport.sportTxt;
        txtSuggestSport.setText(sport);
        scrollWeather.setVisibility(View.VISIBLE);
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=a819414e5f164af28020aec722ee174d";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseTxt = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseTxt);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(
                                            WeatherActivity.this).edit();
                            editor.putString("weather", responseTxt);
                            editor.apply();
                            showWeatherInfo(weather);
                            startService(new Intent(WeatherActivity.this, AutoUpdateService.class));
                        } else {
                            ToastUtil.showToast(WeatherActivity.this,
                                    "获取天气信息失败");
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(WeatherActivity.this,
                                "获取天气信息失败");
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 加载每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(imgPic);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
