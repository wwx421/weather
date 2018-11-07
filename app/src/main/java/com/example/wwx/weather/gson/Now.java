package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwx on 2018/11/3.
 * 解析now
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public NowCond nowCond;

    public class NowCond {

        @SerializedName("txt")
        public String nowCondTxt;
    }
}
