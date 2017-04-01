package com.hultron.lifehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.lifehelper.MainActivity;

import com.hultron.lifehelper.entity.MyUser;
import com.hultron.lifehelper.uitils.ShareUtils;
import com.hultron.lifehelper.uitils.UtilTools;
import com.hultron.lifehelper.view.CustomDialog;
import com.hultron.lifehelper.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登陆
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mRegister;
    private Button mLogin;
    private EditText mUserName, mPassword;
    private TextView mForgetPassword;
    private CheckBox mKeepPass;

    private CustomDialog mCustomDialog;
    private TextView appLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //隐藏系统自带标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
    }

    private void initView() {
        appLabel = (TextView) findViewById(R.id.label);
        UtilTools.setFont(this, appLabel);
        mRegister = (Button) findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.pass);
        mLogin = (Button) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
        mKeepPass = (CheckBox) findViewById(R.id.keep_pass);
        mForgetPassword = (TextView) findViewById(R.id.forget_pass_word);
        mForgetPassword.setOnClickListener(this);

        //设置选中状态
        boolean isChecked = ShareUtils.getBoolean(this, "keeppass", false);
        mKeepPass.setChecked(isChecked);
        if (isChecked) {
            //显示用户名和密码
            mUserName.setText(ShareUtils.getString(this, "name", ""));
            mPassword.setText(ShareUtils.getString(this, "password", ""));
        }

        mCustomDialog = new CustomDialog(this, 200, 200, R.layout.dialog_loading, R.style
                .Theme_dialog, Gravity.CENTER);

        //屏幕外点击无效
        mCustomDialog.setCancelable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                //1.获取输入框的值
                String name = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //2.判断是否为空
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    mCustomDialog.show();
                    //登陆
                    final MyUser user = new MyUser();
                    user.setUsername(name);
                    user.setPassword(password);
                    user.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            mCustomDialog.dismiss();
                            if (e == null) {
                                //判断是否验证邮箱
                                if (user.getEmailVerified()) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity
                                            .class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "请前往邮箱验证", Toast
                                            .LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "登陆失败，用户名或密码错误", Toast
                                        .LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forget_pass_word:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //保存状态
        ShareUtils.putBoolean(this, "keeppass", mKeepPass.isChecked());

        //是否记住密码
        if (mKeepPass.isChecked()) {
            //记住密码
            ShareUtils.putString(this, "name", mUserName.getText().toString().trim());
            ShareUtils.putString(this, "password", mPassword.getText().toString().trim());
        } else {
            //清除密码
            ShareUtils.deleShare(this, "name");
            ShareUtils.deleShare(this, "password");
        }
    }
}
