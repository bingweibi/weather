package com.example.bbw.weather.db;

import org.litepal.crud.DataSupport;

/**
 * 数据库，县级bean
 * Created by bbw on 2017/9/11.
 * @author bibingwei
 */

public class County extends DataSupport{
    public int id;
    public String name;
    public String weather_id;
    public int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
