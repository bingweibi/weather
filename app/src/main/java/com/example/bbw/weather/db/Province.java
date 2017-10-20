package com.example.bbw.weather.db;

import org.litepal.crud.DataSupport;

/**
 * 数据库：省级bean
 * Created by bbw on 2017/9/11.
 * @author bibingwei
 */

public class Province extends DataSupport {
    public int id;
    public String name;
    public int provinceCode;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

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
}
