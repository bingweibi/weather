package com.example.bbw.weather.Gson;


import com.google.gson.annotations.SerializedName;

/**
 * Created by bbw on 2017/9/13.
 */

public class AQI {

    public AQICity city;

    public class AQICity{

        public String pm25;
        @SerializedName("qlty")
        public String quality;
    }
}
