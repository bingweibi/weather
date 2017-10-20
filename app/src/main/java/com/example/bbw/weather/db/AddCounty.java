package com.example.bbw.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by bbw on 2017/10/5.
 * @author bibingwei
 */

public class AddCounty extends DataSupport {
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String countyName;
    public String weatherId;

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
