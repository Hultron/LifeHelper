package com.hultron.lifehelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hultron.lifehelper.entity.MyUser;
import com.hultron.lifehelper.fragment.HelperFragment;
import com.hultron.lifehelper.fragment.GirlsFragment;
import com.hultron.lifehelper.fragment.NewsFragment;
import com.hultron.lifehelper.ui.AboutSoftwareActivity;
import com.hultron.lifehelper.ui.CourierActivity;
import com.hultron.lifehelper.ui.LoginActivity;
import com.hultron.lifehelper.ui.PhoneActivity;
import com.hultron.lifehelper.ui.SettingActivity;
import com.hultron.lifehelper.ui.UserActivity;
import com.hultron.lifehelper.ui.WeatherActivity;
import com.hultron.lifehelper.uitils.UtilTools;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    /*
    * DrawerLayout
    * */
    private DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    NavigationView navView;
    //圆形头像
    CircleImageView mAvatar;

    //TabLayout
    TabLayout mTabLayout;
    //ViewPager
    ViewPager mViewPager;

    //Title
    private List<String> mTitles;
    //Fragment
    private List<Fragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_courier:
                startActivity(new Intent(this, CourierActivity.class));
                return true;
            case R.id.nav_phone_loc:
                startActivity(new Intent(this, PhoneActivity.class));
                return true;
            case R.id.nav_weather:
                startActivity(new Intent(this, WeatherActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化数据
    private void initData() {
        mTitles = new ArrayList<>();
        mTitles.add(getString(R.string.chatting_robot));
        mTitles.add(getString(R.string.update_news));
        mTitles.add(getString(R.string.girls_welfare));

        mFragments = new ArrayList<>();
        mFragments.add(new HelperFragment());
        mFragments.add(new NewsFragment());
        mFragments.add(new GirlsFragment());


    }

    //初始化View
    private void initView() {
        //DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.titlebar_menu);
        }
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        mAvatar = (CircleImageView) headerView.findViewById(R.id.avatar);
        UtilTools.getImageFromShare(this, mAvatar);
        mAvatar.setOnClickListener(this);

        //TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);

        //预加载
        mViewPager.setOffscreenPageLimit(mFragments.size());
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_custom_setting:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.nav_exit:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提示！")
                        .setMessage("是否退出登陆？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //退出登陆
                                MyUser.logOut();   //清除缓存用户对象
                                // 现在的currentUser是null了
                                BmobUser currentUser = BmobUser.getCurrentUser();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create();
                dialog.show();
                return true;
            case R.id.nav_about_software:
                startActivity(new Intent(this, AboutSoftwareActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                startActivity(new Intent(this, UserActivity.class));
                mDrawerLayout.closeDrawers();
                break;
        }
    }
}
