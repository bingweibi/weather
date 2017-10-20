package com.example.bbw.weather.util;

import android.text.TextUtils;

import com.example.bbw.weather.Gson.Weather;
import com.example.bbw.weather.db.City;
import com.example.bbw.weather.db.County;
import com.example.bbw.weather.db.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用于处理服务器返回的数据
 * Created by bbw on 2017/9/11.
 * @author bibingwei
 */

public class Utility {

    final static String TAG = "Utility";

    public static boolean handleProvinceResponse(String response) throws JSONException {

        if (!TextUtils.isEmpty(response)){
            JSONArray allProvince = new JSONArray(response);
            for (int i = 0;i < allProvince.length();i++){
                JSONObject object = allProvince.getJSONObject(i);
                Province province = new Province();
                province.setProvinceCode(object.getInt("id"));
                province.setName(object.getString("name"));
                province.save();
            }
            return true;
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId) throws JSONException {

        if (!TextUtils.isEmpty(response)){
            JSONArray allCities = new JSONArray(response);
            for (int i =0;i < allCities.length();i++){
                JSONObject object = allCities.getJSONObject(i);
                City city = new City();
                city.setCityCode(object.getInt("id"));
                city.setName(object.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) throws JSONException {

        if (!TextUtils.isEmpty(response)){
            JSONArray allCountis = new JSONArray(response);
            for (int i = 0;i<allCountis.length();i++){
                JSONObject object = allCountis.getJSONObject(i);
                County county = new County();
                county.setName(object.getString("name"));
                county.setWeather_id(object.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        }
        return false;
    }

    public static Weather handleWeatherInfoResponse(String response) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }
        return null;
    }
}
