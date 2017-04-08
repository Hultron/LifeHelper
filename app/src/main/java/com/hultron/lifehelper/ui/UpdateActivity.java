package com.hultron.lifehelper.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.uitils.LogUtil;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.kymjs.rxvolley.http.VolleyError;

import java.io.File;


/**
 * 更新
 */
public class UpdateActivity extends BaseActivity {

    private TextView mTvTextSize;
    private String url;
    private String path; //下载文件的存储位置

    //正在下载
    public static final int HANDLER_LOADING = 10001;
    //下载完成
    public static final int HANDLER_OK = 10002;
    //下载失败
    public static final int HANDLER_FAIL = 10003;
    //进度条
    private NumberProgressBar mNumberProgressBar;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_LOADING:
                    //实时更新进度
                    Bundle bundle = msg.getData();
                    long transferedBytes = bundle.getLong("transferedBytes");
                    long totalSize = bundle.getLong("totalSize");
                    mTvTextSize.setText(transferedBytes + "/" + totalSize);
                    //设置进度，百分比形式
                    mNumberProgressBar.setProgress((int) (((float)transferedBytes / (float) totalSize) *
                            100));
                    break;
                case HANDLER_OK:
                    mTvTextSize.setText("下载成功");
                    //启动这个应用安装
                    startInstallApk();
                    break;
                case HANDLER_FAIL:
                    mTvTextSize.setText("下载失败");
                    break;
            }
        }
    };


    private void startInstallApk() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initView();
    }

    private void initView() {
        mNumberProgressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        mNumberProgressBar.setMax(100);

        mTvTextSize = (TextView) findViewById(R.id.tv_size);

        path = getExternalCacheDir() + "/" + System.currentTimeMillis() + ".apk";

        //下载
        url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            //下载
            RxVolley.download(path, url, new ProgressListener() {
                @Override
                public void onProgress(long transferredBytes, long totalSize) {
                    Message message = new Message();
                    message.what = HANDLER_LOADING;
                    Bundle bundle = new Bundle();
                    bundle.putLong("transferedBytes", transferredBytes);
                    bundle.putLong("totalSize", totalSize);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    LogUtil.e("成功");
                    mHandler.sendEmptyMessage(HANDLER_OK);
                }

                @Override
                public void onFailure(VolleyError error) {
                    LogUtil.e("失败");
                    mHandler.sendEmptyMessage(HANDLER_FAIL);
                }
            });
        }
    }
}
