package com.hultron.lifehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.L;

/**
 * 新闻详情
 */

public class NewsContentActivity extends BaseActivity {
    //进度
    private ProgressBar mProgressBar;
    private WebView mNewsContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        initView();
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mNewsContent = (WebView) findViewById(R.id.web_content);

        Intent i = getIntent();
        String title = i.getStringExtra("title");
        final String url = i.getStringExtra("url");

        //设置标题
        Log.e("title", title);
        getSupportActionBar().setTitle(title);
        L.e(url);

        //进行加载网页的逻辑
        //支持javascript
        mNewsContent.getSettings().setJavaScriptEnabled(true);
        //支持缩放
        mNewsContent.getSettings().setSupportZoom(true);
        mNewsContent.getSettings().setBuiltInZoomControls(true);
        //接口回调
        mNewsContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    mNewsContent.setVisibility(View.VISIBLE);
                }
            }
        });
        //加载网页
        mNewsContent.loadUrl(url);
        //本地显示
        mNewsContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        });

    }
}
