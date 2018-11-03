package com.example.wwx.weather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wwx on 2018/11/2.
 * http工具类
 */

public class HttpUtil {

    /**
     * 和服务器进行交互
     *
     * @param address  请求地址
     * @param callback 注册回调处理服务器响应
     */
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
