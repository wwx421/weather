package com.example.wwx.weather.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by wwx on 2018/11/2.
 * 县
 */

public class County extends LitePalSupport {

    public int id;
    public String countyName;
    private String weatherId;
    private int cityId;
}
