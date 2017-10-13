package com.example.bbw.weather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.bbw.weather.Fragments.AboutFragment;
import com.example.bbw.weather.Fragments.AddFragment;
import com.example.bbw.weather.Fragments.HomeFragment;
import com.example.bbw.weather.Fragments.LikeFragment;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup mRadioGroup;
    private RadioButton radio_button_home, radio_button_like, radio_button_about, radio_button_add ;

    private Fragment homeFragment, likeFragment, aboutFragment, addFragment;
    private Fragment mFragment;//当前显示的碎片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment,homeFragment).commit();
        mFragment = homeFragment;
    }

    private void initView() {

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group_button);
        radio_button_home = (RadioButton) findViewById(R.id.radio_button_home);
        radio_button_about = (RadioButton) findViewById(R.id.radio_button_me);
        radio_button_add = (RadioButton) findViewById(R.id.radio_button_add);
        radio_button_like = (RadioButton) findViewById(R.id.radio_button_like);
        mRadioGroup.setOnCheckedChangeListener(this);

        homeFragment = new HomeFragment();
        aboutFragment = new AboutFragment();
        addFragment = new AddFragment();
        likeFragment = new LikeFragment();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup,int checkId) {

        switch (checkId){
            case R.id.radio_button_home:
                radio_button_home.setChecked(true);
                radio_button_about.setChecked(false);
                radio_button_add.setChecked(false);
                radio_button_like.setChecked(false);
                switchFragment(homeFragment);
                break;
            case R.id.radio_button_like:
                radio_button_home.setChecked(false);
                radio_button_about.setChecked(false);
                radio_button_add.setChecked(false);
                radio_button_like.setChecked(true);
                switchFragment(likeFragment);
                break;
            case R.id.radio_button_add:
                radio_button_home.setChecked(false);
                radio_button_about.setChecked(false);
                radio_button_add.setChecked(true);
                radio_button_like.setChecked(false);
                switchFragment(addFragment);
                break;
            case R.id.radio_button_me:
                radio_button_home.setChecked(false);
                radio_button_about.setChecked(true);
                radio_button_add.setChecked(false);
                radio_button_like.setChecked(false);
                switchFragment(aboutFragment);
                break;
        }
    }

    public void switchFragment(Fragment fragment) {
        //判断当前显示的Fragment是不是切换的Fragment
        if(mFragment != fragment) {
            //判断切换的Fragment是否已经添加过
            if (!fragment.isAdded()) {
                //getSupportFragmentManager().popBackStack();
                //如果没有，则先把当前的Fragment隐藏，把切换的Fragment添加上
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment,fragment).addToBackStack(null).commit();
            } else {
                //getSupportFragmentManager().popBackStack();
                //如果已经添加过，则先把当前的Fragment隐藏，把切换的Fragment显示出来
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment,fragment).addToBackStack(null).commit();
            }
            mFragment = fragment;
        }
    }
}
