package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwx on 2018/11/3.
 * 解析
 */

public class ForeCast {

    @SerializedName("date")
    public String date;

    @SerializedName("cond")
    public ForeCastCond foreCastCond;

    @SerializedName("tmp")
    public Temperature temperature;

    public class ForeCastCond {

        @SerializedName("txt_d")
        public String foreCastTxt;
    }

    public class Temperature {

        @SerializedName("max")
        public String temperatureMax;

        @SerializedName("min")
        public String temperatureMin;
    }
}
