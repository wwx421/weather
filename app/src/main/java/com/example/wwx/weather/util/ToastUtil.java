package com.example.wwx.weather.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wwx on 2018/1/31.
 * toast工具类
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(content);
        }
    }
}
