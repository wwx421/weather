package com.example.wwx.weather.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by wwx on 2018/11/2.
 * 市
 */

public class City extends LitePalSupport{

    public int id;
    public String cityName;
    public int cityCode;
    public int provinceId;
}
