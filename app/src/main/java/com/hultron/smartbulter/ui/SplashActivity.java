package com.hultron.smartbulter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hultron.smartbulter.R;
import com.hultron.smartbulter.uitils.ShareUtils;
import com.hultron.smartbulter.uitils.StaticClass;
import com.hultron.smartbulter.uitils.UtilTools;

/**
 * 闪屏页
 */

public class SplashActivity extends AppCompatActivity {

    private TextView mTvSplash;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticClass.HANDLER_SPLASH:
                    //判断程序是否是第一次运行
                    if (isFirst()) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                    break;

            }
        }
    };

    /*
    * 1.延时2000ms
    * 2.判断程序是否第一次
    * 3.自定义字体
    * 4.Activity全屏主题
    * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        initView();
    }

    private void initView() {
        //延时2s
        mHandler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH, 2000);

        mTvSplash = (TextView) findViewById(R.id.tv_splash);

//        //设置字体
//        Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/FONT.TTF");
//        mTvSplash.setTypeface(fontType);
        UtilTools.setFont(this, mTvSplash);
    }

    //判断程序是否第一次运行
    private boolean isFirst() {
        boolean isFirst = ShareUtils.getBoolean(this, StaticClass.SHARE_IS_FIRST, true);
        if (isFirst) {
            ShareUtils.putBoolean(this, StaticClass.SHARE_IS_FIRST, false);
            //是第一次运行
            return true;
        } else {
            //不是第一次运行
            return false;
        }
    }

    //禁止返回键
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }
}
