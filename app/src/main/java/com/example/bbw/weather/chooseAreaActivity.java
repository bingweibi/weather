package com.example.bbw.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbw.weather.db.City;
import com.example.bbw.weather.db.County;
import com.example.bbw.weather.db.Province;
import com.example.bbw.weather.util.HttpUtility;
import com.example.bbw.weather.util.Utility;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author bibingwei
 */

public class chooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private Button btn_Back;
    private TextView title_Text;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);

        btn_Back = (Button) findViewById(R.id.button_back);
        title_Text = (TextView) findViewById(R.id.title_id);
        listView = (ListView) findViewById(R.id.list_view);
        btn_Back = (Button) findViewById(R.id.button_back);
        //btn_Add = (Button) findViewById(R.id.button_add);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(i).getWeather_id();
                    Log.d("测试天气id",weatherId);
                    Intent intent = new Intent(getApplicationContext(),ShowWeatherInfo.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_PROVINCE){
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel == LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
    }

    private void queryProvince() {
        title_Text.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String url = "http://guolin.tech/api/china";
            queryFromServer(url,"province");
        }
    }

    private void queryCounty() {
        title_Text.setText(selectedCity.getName());
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county:countyList){
                Log.d("ceshi1111111",county.getName());
                dataList.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            Log.d("ceshi22222222222",url);
            queryFromServer(url ,"county");
        }
    }

    private void queryCity() {
        title_Text.setText(selectedProvince.getName());
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city: cityList){
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            Log.d("ceshi3333333","" + provinceCode);
            String url = "http://guolin.tech/api/china/"+ provinceCode ;
            queryFromServer(url,"city");
        }

    }

    private void queryFromServer(String url, final String type) {
        HttpUtility.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                String province = "province";
                String city = "city";
                String county = "county";
                if (province.equals(type)){
                    try {
                        result = Utility.handleProvinceResponse(responseText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (city.equals(type)){
                    try {
                        result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (county.equals(type)){
                    try {
                        result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }
}
