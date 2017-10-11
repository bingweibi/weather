package com.example.bbw.weather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bbw on 2017/9/13.
 */

public class Now {
    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String nowWeather;
    }
    @SerializedName("tmp")
    public String nowTemp;

    @SerializedName("hum")
    public String humidity;

    @SerializedName("fl")
    public String tempFeel;
}
