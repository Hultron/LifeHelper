package com.hultron.smartbulter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hultron.smartbulter.fragment.ButlerFragment;
import com.hultron.smartbulter.fragment.GirlsFragment;
import com.hultron.smartbulter.fragment.UserFragment;
import com.hultron.smartbulter.fragment.NewsFragment;
import com.hultron.smartbulter.ui.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    //TabLayout
    private TabLayout mTabLayout;
    //ViewPager
    private ViewPager mViewPager;
    //Title
    private List<String> mTitles;
    //Fragment
    private List<Fragment> mFragments;
    //悬浮按钮
    private FloatingActionButton mFabSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //去掉阴影
        getSupportActionBar().setElevation(0);

        initData();
        initView();

    }

    //初始化数据
    private void initData() {
        mTitles = new ArrayList<>();
        mTitles.add(getString(R.string.service_butler));
        mTitles.add(getString(R.string.wechat_news));
        mTitles.add(getString(R.string.girls_community));
        mTitles.add(getString(R.string.user_center));

        mFragments = new ArrayList<>();
        mFragments.add(new ButlerFragment());
        mFragments.add(new NewsFragment());
        mFragments.add(new GirlsFragment());
        mFragments.add(new UserFragment());


    }

    //初始化View
    private void initView() {

        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mFabSetting = (FloatingActionButton) findViewById(R.id.fab_setting);
        mFabSetting.setVisibility(View.GONE);
        mFabSetting.setOnClickListener(this);

        //预加载
        mViewPager.setOffscreenPageLimit(mFragments.size());

        //ViewPager 滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: " + position);
                if (position == 0) {
                    mFabSetting.setVisibility(View.GONE);
                } else {
                    mFabSetting.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            //选中的 item
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            //返回 items 的个数
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        });

        //绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }
}
