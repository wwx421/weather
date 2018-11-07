package com.example.wwx.weather.util;

import android.text.TextUtils;

import com.example.wwx.weather.db.City;
import com.example.wwx.weather.db.County;
import com.example.wwx.weather.db.Province;
import com.example.wwx.weather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wwx on 2018/11/2.
 * 解析JSON数据工具类
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param response 省级JSON数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.provinceName = provinceObject.getString("name");
                    province.provinceCode = provinceObject.getInt("id");
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的JSON数据
     *
     * @param provinceId 省级id
     * @param response   市级JSON数据
     */
    public static boolean handleCityResponse(int provinceId, String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.cityName = cityObject.getString("name");
                    city.cityCode = cityObject.getInt("id");
                    city.provinceId = provinceId;
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理县级JSON数据
     *
     * @param cityId   市级id
     * @param response 县级JSON数据
     */
    public static boolean handleCountyResponse(int cityId, String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.countyName = countyObject.getString("name");
                    county.cityId = cityId;
                    county.weatherId = countyObject.getString("weather_id");
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将json数据解析成weather实体类
     * @param response json数据
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
