package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wwx on 2018/11/3.
 * 天气总实体类
 */

public class Weather {

    public String status;
    public Basic basic;
    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<ForeCast> foreCastList;
}
