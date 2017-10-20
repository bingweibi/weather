package com.example.bbw.weather.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbw.weather.Eventbus.Event;
import com.example.bbw.weather.Gson.DailyForecast;
import com.example.bbw.weather.Gson.HourlyForecast;
import com.example.bbw.weather.Gson.Weather;
import com.example.bbw.weather.R;
import com.example.bbw.weather.util.HttpUtility;
import com.example.bbw.weather.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Integer.parseInt;

/**
 * Created by bbw on 2017/9/16.
 * @author bibingwei
 */

public class HomeFragment extends Fragment {

    private TextView text_cityName;
    private TextView text_tempNow;
    private ImageView icon_Weather;
    private TextView text_pm25;
    private TextView text_quality;
    private TextView text_sunRaise;
    private TextView text_sunDown;
    private TextView text_rain;
    private TextView text_humidity;
    private TextView text_visibility;
    private TextView text_wind;
    private TextView text_todayDescriable;
    private TextView text_hour;
    private TextView text_hourWeather;
    private TextView text_Date;
    private TextView text_minTemp;
    private TextView text_maxTemp;
    //分
    private LinearLayout dayForecastLayout;
    private LinearLayout hourForecastLayout;
    //总
    private LinearLayout dailyForecastLayout;
    private LinearLayout hourlyForecastLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String weatherId2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_layout_fragment,null);
        text_cityName = view.findViewById(R.id.text_cityName);
        text_tempNow = view.findViewById(R.id.text_tempNow);
        icon_Weather = view.findViewById(R.id.icon_Weather);
        text_pm25 = view.findViewById(R.id.text_pm25);
        text_quality = view.findViewById(R.id.text_quality);
        text_sunRaise = view.findViewById(R.id.text_sunRaise);
        text_sunDown = view.findViewById(R.id.text_sunDown);
        text_rain = view.findViewById(R.id.text_rain);
        text_humidity = view.findViewById(R.id.text_humidity);
        text_visibility = view.findViewById(R.id.text_visibility);
        text_wind = view.findViewById(R.id.text_wind);
        text_todayDescriable = view.findViewById(R.id.text_todayDescriable);
        dailyForecastLayout = view.findViewById(R.id.layout_forecastWeather);
        hourlyForecastLayout = view.findViewById(R.id.layout_hourlyForecastWeather);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherInfo = sharedPreferences.getString("weatherInfo",null);

        //解析缓存
        if (weatherInfo != null){
            try {
                Weather weather = Utility.handleWeatherInfoResponse(weatherInfo);
                Log.d("测试缓存中城市名",weather.basic.cityName);
                //mWeatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (weatherId2 !=null){
            requestWeatherInfo(weatherId2);
        }else{
            requestWeatherInfo("CN101010100");
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeatherInfo(sharedPreferences.getString("countyId",null));
            }
        });
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void eventBus(Event event){
        weatherId2 = event.getMessage();
        requestWeatherInfo(weatherId2);
        //EventBus.getDefault().cancelEventDelivery(event);
    }

    private void requestWeatherInfo(String weatherId){

        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=6e34fb664b2d421cabf16561b3b3048d";
        HttpUtility.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity().getApplicationContext(), "请求天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherInfoResponse(responseText);

                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && weather.status.equals("ok")){
                                //缓存天气信息
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                editor.putString("weatherInfo",responseText);
                                editor.putString("countyId",weatherId2);
                                editor.apply();
                                showWeatherInfo(weather);
                                Log.d("测试城市名",weather.basic.cityName);
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        text_cityName.setText(weather.basic.cityName);
        String weatherCode = weather.now.cond.code;
        int weatherIcon = getResources().getIdentifier("ic_"+weatherCode,"drawable",getActivity().getPackageName());
        text_tempNow.setText(weather.now.nowTemp+ "℃");
        icon_Weather.setImageResource(weatherIcon);
        dailyForecastLayout.removeAllViews();
        for (DailyForecast dailyForecast : weather.dailyForecasts){
            dayForecastLayout = new LinearLayout(getContext());
            text_Date = new TextView(getContext());
            text_minTemp = new TextView(getContext());
            text_maxTemp = new TextView(getContext());
            text_minTemp.setText("min: "+dailyForecast.tmp.minTemp);
            text_maxTemp.setText("max: "+dailyForecast.tmp.maxTemp);
            text_Date.setText(dailyForecast.date.trim().substring(5,10));
            text_Date.setTextSize(17);
            text_minTemp.setTextSize(17);
            text_maxTemp.setTextSize(17);
            dayForecastLayout.setOrientation(LinearLayout.VERTICAL);
            dayForecastLayout.setPadding(40,10,40,10);
            dayForecastLayout.addView(text_Date);
            dayForecastLayout.addView(text_minTemp);
            dayForecastLayout.addView(text_maxTemp);
            dailyForecastLayout.addView(dayForecastLayout);
        }

        hourlyForecastLayout.removeAllViews();
        for (HourlyForecast hourlyForecast : weather.hourlyForecasts){

            hourForecastLayout = new LinearLayout(getContext());
            text_hour = new TextView(getContext());
            text_hourWeather = new TextView(getContext());
            text_hour.setText(hourlyForecast.date.trim().substring(10,16));
            text_hourWeather.setText(hourlyForecast.cond.hourlyWeather);
            text_hour.setTextSize(15);
            text_hourWeather.setTextSize(15);
            hourForecastLayout.setOrientation(LinearLayout.VERTICAL);
            hourForecastLayout.setPadding(20,10,20,10);
            hourForecastLayout.addView(text_hour);
            hourForecastLayout.addView(text_hourWeather);
            hourlyForecastLayout.addView(hourForecastLayout);
        }

        for (DailyForecast dailyForecast2 : weather.dailyForecasts){
            text_sunRaise.setText(dailyForecast2.astro.sunRaise);
            text_sunDown.setText(dailyForecast2.astro.sunDown);
            text_rain.setText(dailyForecast2.rain);
            text_humidity.setText(dailyForecast2.humidity);
            text_visibility.setText(dailyForecast2.vis);
            text_wind.setText(dailyForecast2.wind.wind);
            break;
        }
        text_todayDescriable.setText(weather.suggestion.comf.descriable);

        if (weather.aqi != null){
            Log.d("weather.aqi",""+weather.aqi);
            text_pm25.setText(weather.aqi.city.pm25);
            text_quality.setText(weather.aqi.city.quality);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weatherInfo = sharedPreferences.getString("weatherInfo",null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
