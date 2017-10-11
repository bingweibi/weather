package com.example.bbw.weather.db;

import org.litepal.crud.DataSupport;

/**
 * 数据库，市级bean
 * Created by bbw on 2017/9/11.
 */

public class City extends DataSupport{
    public int id;
    public String name;
    public int CityCode;

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }

    public int provinceId;

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

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
