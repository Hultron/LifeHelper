package com.hultron.lifehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.ShareUtil;
import com.hultron.lifehelper.uitils.StaticClass;
import com.hultron.lifehelper.uitils.UtilTools;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 闪屏页
 */

public class SplashActivity extends AppCompatActivity {

    TextView mTvSplash;
    ImageView mSplashBg;

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
        mSplashBg = (ImageView)findViewById(R.id.splash_bg);
        UtilTools.setBackground(this, mSplashBg);
        //延时2000ms
        mHandler.sendEmptyMessageDelayed(StaticClass.HANDLER_SPLASH, 2000);

        mTvSplash = (TextView) findViewById(R.id.tv_splash);
        UtilTools.setFont(this, mTvSplash);
    }

    //判断程序是否第一次运行
    private boolean isFirst() {
        boolean isFirst = ShareUtil.getBoolean(this, StaticClass.SHARE_IS_FIRST, true);
        if (isFirst) {
            ShareUtil.putBoolean(this, StaticClass.SHARE_IS_FIRST, false);
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

    public void setSplashBackground(final ImageView image) {
        String backgroungUrl = "http://open.iciba.com/dsapi/";
        RxVolley.get(backgroungUrl, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    String bgImage = jsonObject.getString("picture2");
                    Picasso.with(SplashActivity.this).load(bgImage).into(image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
            }
        });
    }
}
