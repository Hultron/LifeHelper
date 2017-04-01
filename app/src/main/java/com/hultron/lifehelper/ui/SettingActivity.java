package com.hultron.lifehelper.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.service.SmsService;
import com.hultron.lifehelper.uitils.L;
import com.hultron.lifehelper.uitils.ShareUtils;
import com.hultron.lifehelper.uitils.StaticClass;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设置
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final int QRCODE_RESULT = 1111;
    private static final int REQUEST_CAMERA_CODE = 1112;
    //语音播报
    private Switch mSwSpeack;
    //短信提醒
    private Switch mSwSms;
    //6.0及以上系统申请SYSTEM_ALERT_WINDOW权限码
    private static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    //检测更新
    private LinearLayout mUpdate;
    private TextView mTvVersion;
    private String versionName;
    private int versionCode;

    //二维码扫描
    private LinearLayout mQrcodeScan;
    private TextView mScanResult;//扫描结果
    //生成二维码
    private LinearLayout mQrcodeShare;

    //我的位置
    private LinearLayout mMyLocation;

    //关于软件
    private LinearLayout mAboutSoftware;

    private String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        //是否语音
        mSwSpeack = (Switch) findViewById(R.id.sw_speak);
        boolean isSpeak = ShareUtils.getBoolean(this, "isSpeak", false);
        mSwSpeack.setChecked(isSpeak);
        mSwSpeack.setOnClickListener(this);

        //是否短信
        mSwSms = (Switch) findViewById(R.id.sw_sms);
        boolean isSms = ShareUtils.getBoolean(this, "isSms", false);
        mSwSms.setChecked(isSms);
        mSwSms.setOnClickListener(this);

        //检查版本
        mUpdate = (LinearLayout) findViewById(R.id.ll_update);
        mUpdate.setOnClickListener(this);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        try {
            getVersionNameAndCode();
            mTvVersion.setText(String.format("当前版本：%s", versionName));
        } catch (PackageManager.NameNotFoundException e) {
            mTvVersion.setText("无法获取当前版本，请检查网络");
        }

        //扫一扫
        mQrcodeScan = (LinearLayout) findViewById(R.id.qrcode_scan);
        mQrcodeScan.setOnClickListener(this);
        mScanResult = (TextView) findViewById(R.id.tv_scan_result);

        //生成二维码
        mQrcodeShare = (LinearLayout) findViewById(R.id.qrcode_share);
        mQrcodeShare.setOnClickListener(this);

        //我的位置
        mMyLocation = (LinearLayout) findViewById(R.id.my_location);
        mMyLocation.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_speak:
                //切换相反
                mSwSpeack.setSelected(!mSwSpeack.isChecked());
                //保存状态
                ShareUtils.putBoolean(this, "isSpeak", mSwSpeack.isChecked());
                break;
            case R.id.sw_sms:
                if (Build.VERSION.SDK_INT > 22) {
                    requestDrawOverLays();
                }
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECEIVE_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            StaticClass.SMS_PER_CODE);
                } else {
                    switchSms();
                }
                break;
            case R.id.ll_update:
                /*步骤：
                * 1.请求服务器的配置文件，拿到code
                * 2.比较
                * 3.dialog
                * 4.跳转到更新界面并且把url传递过去*/
                RxVolley.get(StaticClass.CHECK_UPDATE_URL, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        L.i(t);
                        parsingJson(t);
                    }
                });
                break;
            case R.id.qrcode_scan:
                //打开扫描界面扫描条形码或二维码
                if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission
                        .CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest
                            .permission.CAMERA}, REQUEST_CAMERA_CODE);
                } else {
                    Intent openCameraIntent = new Intent(SettingActivity.this, CaptureActivity
                            .class);
                    startActivityForResult(openCameraIntent, QRCODE_RESULT);
                }
                break;
            case R.id.qrcode_share:
                startActivity(new Intent(this, QrCodeShareActivity.class));
                break;
            case R.id.my_location:
                startActivity(new Intent(this, LocationActivity.class));
                break;
        }
    }


    private void parsingJson(String t) {
        try {
            JSONObject jsonObject = new JSONObject(t);
            int code = jsonObject.getInt("versionCode");
            String content = jsonObject.getString("content");
            if (code > versionCode) {
                url = jsonObject.getString("url");
                showUpdateDialog(content);
            } else {
                Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //弹出升级提示
    private void showUpdateDialog(String content) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("有新版本啦！")
                .setMessage("修复了一些bug")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //这里什么都不做，也会执行dismiss()
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SettingActivity.this, UpdateActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }

    //获取版本号/Code
    private void getVersionNameAndCode() throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
        versionName = info.versionName;
        versionCode = info.versionCode;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case StaticClass.SMS_PER_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    switchSms();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case QRCODE_RESULT:
                Intent openCameraIntent = new Intent(SettingActivity.this, CaptureActivity
                        .class);
                startActivityForResult(openCameraIntent, QRCODE_RESULT);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void switchSms() {
        //切换相反
        mSwSms.setSelected(!mSwSms.isChecked());
        //保存状态
        ShareUtils.putBoolean(this, "isSms", mSwSms.isChecked());
        if (mSwSms.isChecked()) {
            startService(new Intent(this, SmsService.class));
        } else {
            stopService(new Intent(this, SmsService.class));
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void requestDrawOverLays() {
        if (!Settings.canDrawOverlays(SettingActivity.this)) {

            AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                    .setTitle("需要您手动授予权限")
                    .setMessage("点击 OK 进入设置页面手动开启权限，允许本应用在其他应用界面之上打开")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(i, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSwSms.setChecked(false);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                //SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(this, "Permission Denied by user.", Toast.LENGTH_SHORT)
                        .show();
                mSwSms.setChecked(false);
            } else {
                Toast.makeText(this, "Permission allowed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == QRCODE_RESULT) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                mScanResult.setText(scanResult);
            }
        }
    }
}
