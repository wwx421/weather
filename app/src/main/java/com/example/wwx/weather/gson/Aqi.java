package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwx on 2018/11/3.
 * 解析aqi
 */

public class Aqi {

    @SerializedName("city")
    public AqiCity aqiCity;

    public class AqiCity {

        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }
}
