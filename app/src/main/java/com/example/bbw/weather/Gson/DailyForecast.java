package com.example.bbw.weather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bbw on 2017/9/13.
 */

public class DailyForecast {

    public Astro astro;

    public class Astro {

        @SerializedName("sr")
        public String sunRaise;

        @SerializedName("ss")
        public String sunDown;
    }

    public Cond cond;

    public class Cond {
        @SerializedName("txt_d")
        public String  weatherDaytime;
        @SerializedName("txt_n")
        public String weatherNight;
    }

    public String date;

    @SerializedName("pop")
    public String rain;

    public Tmp tmp;

    public class Tmp {
        @SerializedName("max")
        public String maxTemp;
        @SerializedName("min")
        public String minTemp;
    }
    public String vis;//能见度

    public Wind wind;

    public class Wind {
        @SerializedName("sc")
        public String wind;
    }

    @SerializedName("hum")
    public String humidity;//相对湿度
}
