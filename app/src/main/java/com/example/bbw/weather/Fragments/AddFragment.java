package com.example.bbw.weather.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bbw.weather.R;
import com.example.bbw.weather.db.AddCounty;
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
 * Created by bbw on 2017/9/16.
 */

public class AddFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private Button btn_Back;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private Fragment likeFragment;
    private Fragment addFragment;
    private FragmentManager manager;
    private FragmentTransaction fragmentTransaction;
    private County countyName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_layout_fragment,null);
        likeFragment = new LikeFragment();
        addFragment = new AddFragment();
        btn_Back = view.findViewById(R.id.button_back);
        listView = view.findViewById(R.id.list_view);
        btn_Back = view.findViewById(R.id.button_back);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        likeFragment = new LikeFragment();
        fragmentTransaction = manager.beginTransaction();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    btn_Back.setVisibility(View.INVISIBLE);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(i).getWeather_id();
                    countyName = countyList.get(i);
                    String countiesName = countyList.get(i).getName();
                    Log.d("测试天气id",weatherId);

                    //DataSupport.deleteAll(AddCounty.class);
                    Boolean test = false;
                    AddCounty addCounty = new AddCounty();
                    addCounty.setCountyName(countiesName);
                    addCounty.setWeatherId(weatherId);
                    List<AddCounty> addCountyList = DataSupport.findAll(AddCounty.class);
                    for (AddCounty addCounty1 : addCountyList){
                        if (addCounty1.getCountyName().contains(countiesName)){
                            test = true;
                        }
                    }
                    if (!test)
                        addCounty.save();

                    //EventBus.getDefault().postSticky(new Event(countyName));
                    fragmentTransaction.replace(R.id.main_fragment,likeFragment);
                    //fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.remove(addFragment);
//                    fragmentTransaction.hide(addFragment).add(R.id.main_fragment,likeFragment).commit();
                    fragmentTransaction.commit();
                    Log.d("测试fragment跳转","success");
                }
            }
        });
        queryProvince();
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel == LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        return view;
    }

    private void queryProvince() {

        btn_Back.setVisibility(View.GONE);
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

        btn_Back.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county:countyList){
                //Log.d("ceshi1111111",county.getName());
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

        btn_Back.setVisibility(View.VISIBLE);
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
            //Log.d("ceshi3333333","" + provinceCode);
            String url = "http://guolin.tech/api/china/"+ provinceCode ;
            queryFromServer(url,"city");
        }

    }

    private void queryFromServer(String url, final String type) {

        HttpUtility.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    try {
                        result = Utility.handleProvinceResponse(responseText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if ("city".equals(type)){
                    try {
                        result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if ("county".equals(type)){
                    try {
                        result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
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
