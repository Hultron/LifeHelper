package com.hultron.lifehelper.ui;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Activity基类
 * <p>
 * 主要做的事情：
 * 1.统一的属性
 * 2.统一的接口
 * 3.统一的方法
 */

public class BaseActivity extends AppCompatActivity {

    private PermissionsResultListener mListener;

    private int mRequestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //去除阴影
        getSupportActionBar().setElevation(0);
    }

    //菜单栏操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * 运行时权限封装
    * */

    //定义回调接口
    public interface PermissionsResultListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    /*
    * 其他Activity继承BaseActivity调用PerformRequestPermissions方法
    *
    * @param desc 首次申请权限被拒绝后再次申请给用户的描述提示
    * @param permissions 要申请的权限数组
    * @param requestCode 申请标记值
    * @param listener 实现的接口
    * */
    protected void performRequestPermissions(String desc, String[] permissions, int requestCode,
                                             PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {//检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {//已经申请权限
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }

    }

    /*
    * 检查每个权限是否申请
    * @return ture 需要申请权限，false 已经申请权限
    * */
    private boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /*
    * 申请权限前判断是否需要声明
    * */
    @TargetApi(23)
    private void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            showRationalDialog(desc, permissions, requestCode);
        } else {
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
        }
    }


    /*
    * 弹出声明的Dialog
    * */
    private void showRationalDialog(String desc, final String[] permissions, final int
            requestCode) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("申请权限的原因")
                .setMessage(desc)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(BaseActivity.this, permissions,
                                requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /*
    * 再次申请权限时，是否需要声明
    * */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    /*
    * 申请权限结果的回调
    * */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (mListener == null) {
                return;
            }
            if (checkEachPermissionsGranted(grantResults)) {

                mListener.onPermissionGranted();

            } else {//用户拒绝权限

                mListener.onPermissionDenied();

            }
        }
    }

    /*
    * 检查回调结果
    * */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}

