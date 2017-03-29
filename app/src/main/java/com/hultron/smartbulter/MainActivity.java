package com.hultron.smartbulter;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hultron.smartbulter.entity.MyUser;
import com.hultron.smartbulter.fragment.ButlerFragment;
import com.hultron.smartbulter.fragment.GirlsFragment;
import com.hultron.smartbulter.fragment.UserFragment;
import com.hultron.smartbulter.fragment.NewsFragment;

import com.hultron.smartbulter.ui.AboutSoftwareActivity;
import com.hultron.smartbulter.ui.CourierActivity;
import com.hultron.smartbulter.ui.LoginActivity;
import com.hultron.smartbulter.ui.PhoneActivity;
import com.hultron.smartbulter.ui.SettingActivity;
import com.hultron.smartbulter.ui.UserActivity;
import com.hultron.smartbulter.uitils.ShareUtils;
import com.hultron.smartbulter.uitils.UtilTools;
import com.hultron.smartbulter.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_WRITE_SETTINGS = 0;
    /*
    * DrawerLayout
    * */
    private DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    NavigationView navView;
    //圆形头像
    private CircleImageView mAvatar;
    private RelativeLayout mNavHeader;
    private CustomDialog mDialog;
    //TabLayout
    TabLayout mTabLayout;
    //ViewPager
    ViewPager mViewPager;
    //Title
    private List<String> mTitles;
    //Fragment
    private List<Fragment> mFragments;
    //悬浮按钮
    private FloatingActionButton mFabSetting;

    private Button mConfirmUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
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

        //DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.titlebar_menu);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        mAvatar = (CircleImageView) headerView.findViewById(R.id.avatar);
        UtilTools.getImageFromShare(this, mAvatar);
        mAvatar.setOnClickListener(this);

        //TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

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
            case R.id.nav_courier:
                startActivity(new Intent(this, CourierActivity.class));
                return true;
            case R.id.nav_phone_loc:
                startActivity(new Intent(this, PhoneActivity.class));
                return true;
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
