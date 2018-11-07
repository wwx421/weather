package com.example.wwx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wwx on 2018/11/3.
 * 解析suggestion
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String comfortTxt;
    }

    public class CarWash {

        @SerializedName("txt")
        public String carWashTxt;
    }

    public class Sport {

        @SerializedName("txt")
        public String sportTxt;
    }
}
