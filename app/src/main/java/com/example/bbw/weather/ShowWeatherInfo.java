package com.example.bbw.weather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbw.weather.Gson.DailyForecast;
import com.example.bbw.weather.Gson.HourlyForecast;
import com.example.bbw.weather.Gson.Weather;
import com.example.bbw.weather.util.HttpUtility;
import com.example.bbw.weather.util.Utility;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.LitePalApplication.getContext;

/**
 * @author bibingwei
 */

public class ShowWeatherInfo extends AppCompatActivity {

    /**
     * 城市名
     */
    public TextView text_cityName;
    /**
     * now 温度+
     */
    //
    public TextView text_tempNow;
    /**
     * 今天pm2.5+
     */
    public TextView text_pm25;
    /**
     * 今天天气质量+
     */
    public TextView text_quality;
    /**
     * 今天日出时间+
     */
    public TextView text_sunRaise;
    /**
     * 今天日落时间+
     */
    public TextView text_sunDown;
    /**
     * 今天降雨概率+
     */
    public TextView text_rain;
    /**
     * 今天相对湿度+
     */
    public TextView text_humidity;
    /**
     * 今天能见度+
     */
    public TextView text_visibility;
    /**
     * 今天风向+
     */
    public TextView text_wind;
    /**
     * 今天天气状况描述+
     */
    public TextView text_nowWeather;
    public TextView text_todayDescrible;

    private LinearLayout forecastLayout;
    private LinearLayout hourlyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather_info);

        text_cityName = (TextView) findViewById(R.id.text_cityName);
        text_tempNow = (TextView) findViewById(R.id.text_tempNow);
        text_pm25 = (TextView) findViewById(R.id.text_pm25);
        text_quality = (TextView) findViewById(R.id.text_quality);
        text_sunRaise = (TextView) findViewById(R.id.text_sunRaise);
        text_sunDown = (TextView) findViewById(R.id.text_sunDown);
        text_rain = (TextView) findViewById(R.id.text_rain);
        text_humidity = (TextView) findViewById(R.id.text_humidity);
        text_visibility = (TextView) findViewById(R.id.text_visibility);
        text_wind = (TextView) findViewById(R.id.text_wind);
        text_nowWeather = (TextView) findViewById(R.id.text_nowWeather);
        text_todayDescrible = (TextView) findViewById(R.id.text_todayDescriable);
        forecastLayout = (LinearLayout) findViewById(R.id.layout_forecastWeather);
        hourlyLayout = (LinearLayout) findViewById(R.id.layout_hourWeather);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherInfo = sharedPreferences.getString("weatherInfo",null);

        //解析缓存
        if (weatherInfo != null){
            try {
                Weather weather = Utility.handleWeatherInfoResponse(weatherInfo);
                Log.d("测试缓存中城市名",weather.basic.cityName);
                showWeatherInfo(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
                String weatherId = getIntent().getStringExtra("weather_id");
                requestWeatherInfo(weatherId);
        }
    }

    private void requestWeatherInfo(final String weatherId) {

        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=6e34fb664b2d421cabf16561b3b3048d";
        Log.d("测试url",weatherUrl);
        HttpUtility.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowWeatherInfo.this,"请求天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherInfoResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && weather.status.equals("ok")){
                                //缓存天气信息
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ShowWeatherInfo.this).edit();
                                editor.putString("weatherInfo",responseText);
                                editor.apply();
                                showWeatherInfo(weather);
                                Log.d("测试城市名",weather.basic.cityName);
                            }else{
                                Toast.makeText(ShowWeatherInfo.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                            }
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
        text_tempNow.setText(weather.now.nowTemp);
        text_pm25.setText(weather.aqi.city.pm25);
        text_quality.setText(weather.aqi.city.quality);
        text_todayDescrible.setText(weather.suggestion.comf.descriable);

    }

}
