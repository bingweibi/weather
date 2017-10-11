package com.example.bbw.weather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bbw on 2017/9/13.
 */

public class HourlyForecast {

    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String hourlyWeather;
    }
    @SerializedName("pop")
    public String rain;

    public String date;

    @SerializedName("tmp")
    public String hourTemp;

}
