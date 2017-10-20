package com.example.bbw.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 网络请求工具
 * Created by bbw on 2017/9/11.
 * @author bibingwei
 */

public class HttpUtility {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callBack){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callBack);
    }
}
