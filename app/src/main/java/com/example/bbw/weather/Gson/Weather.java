package com.example.bbw.weather.Gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bbw on 2017/9/14.
 */

public class Weather {

    public AQI aqi;
    public String status;
    public Basic basic;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecasts;

    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecasts;
}
