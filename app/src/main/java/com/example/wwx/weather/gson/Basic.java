package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwx on 2018/11/3.
 * 解析basic
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("update")
    public update update;

    public class update{
        @SerializedName("loc")
        public String updateTime;
    }
}
