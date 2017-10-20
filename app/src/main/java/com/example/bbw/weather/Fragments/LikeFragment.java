package com.example.bbw.weather.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.bbw.weather.Eventbus.Event;
import com.example.bbw.weather.R;
import com.example.bbw.weather.db.AddCounty;
import com.example.bbw.weather.db.County;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bbw on 2017/9/16.
 * @author bibingwei
 */

public class LikeFragment extends Fragment implements AMapLocationListener {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private List<County> countyList;
    private Fragment homeFragment;
    private Fragment likeFragment;
    private FragmentManager manager;
    private FragmentTransaction fragmentTransaction;
    private List<AddCounty> addCountyList = new ArrayList<>();

    /***
     * 定位需要的声明
     *定位发起端
     */
    private AMapLocationClient mLocationClient = null;
    /**
     * 定位参数
     */
    private AMapLocationClientOption mLocationOption = null;
    /**
     * 用于判断是否只显示一次定位信息和用户重新定位
     */
    private boolean isFirstLoc = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
        Log.d("注册","success");
        initLoc();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation!= null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                //df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                if (isFirstLoc) {
                    //获取定位信息
                    Boolean test = false;
                    AddCounty addCounty = new AddCounty();
                    addCounty.setCountyName(amapLocation.getDistrict());
                    addCounty.setWeatherId(amapLocation.getLatitude()+","+amapLocation.getLongitude());
                    List<AddCounty> addCountyList = DataSupport.findAll(AddCounty.class);
                    for (AddCounty addCounty1 : addCountyList){
                        if (addCounty1.getCountyName().contains(amapLocation.getDistrict())){
                            test = true;
                        }
                    }
                    if (!test) {
                        addCounty.save();
                    }
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.like_layout_fragment,null);
        homeFragment = new HomeFragment();
        likeFragment = new LikeFragment();
        listView = view.findViewById(R.id.likeCounty);
        dataList = new ArrayList<>();
        //countyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        fragmentTransaction = manager.beginTransaction();

        addCountyList = DataSupport.findAll(AddCounty.class);
        if (addCountyList.size() > 0){
            for (AddCounty county:addCountyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String weatherId = addCountyList.get(position).getWeatherId();
                EventBus.getDefault().postSticky(new Event(weatherId));
                fragmentTransaction.replace(R.id.main_fragment,homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                new AlertDialog.Builder(getActivity()).setTitle("Alert")
                        .setMessage("确定删除？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DataSupport.deleteAll(AddCounty.class,"countyName = ?",addCountyList.get(position).getCountyName());
                                dataList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("否",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
                return true;
            }
        });
        return view;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
//    public void onEvent(Event event){
//        //dataList.clear();
//        Log.d("接受消息","success");
//        countyName = event.getMessage();
//        countyList.add(countyName);
//        dataList.add(countyName.getName());
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }

    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
}
