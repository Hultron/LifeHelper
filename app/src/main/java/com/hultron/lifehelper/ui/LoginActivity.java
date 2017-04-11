package com.hultron.lifehelper.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hultron.lifehelper.MainActivity;
import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.MyUser;
import com.hultron.lifehelper.uitils.ShareUtil;
import com.hultron.lifehelper.uitils.StaticClass;
import com.hultron.lifehelper.uitils.UtilTools;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.http.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登陆
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button mRegister;
    Button mLogin;
    private EditText mUserName, mPassword;
    TextView mForgetPassword;
    private CheckBox mKeepPass;
    private ProgressDialog mProgressDialog;
    public TextView appLabel;
    //private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }


    private void initView() {
        mRegister = (Button) findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);
        mUserName = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.pass);
        mLogin = (Button) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
        mKeepPass = (CheckBox) findViewById(R.id.keep_pass);
        mForgetPassword = (TextView) findViewById(R.id.forget_pass_word);
        mForgetPassword.setOnClickListener(this);

        appLabel = (TextView) findViewById(R.id.label);
        UtilTools.setFont(this, appLabel);
        setAppLabel(appLabel);

        //设置选中状态
        boolean isChecked = ShareUtil.getBoolean(this, StaticClass.KEEP_PASS, false);
        mKeepPass.setChecked(isChecked);
        if (isChecked) {
            //显示用户名和密码
            mUserName.setText(ShareUtil.getString(this, "name", ""));
            mPassword.setText(ShareUtil.getString(this, "password", ""));
            mUserName.setSelection(mUserName.length());
        }

        //        mCustomDialog = new CustomDialog(this, 400, 400, R.layout.dialog_loading, R.style
        //                .Theme_dialog, Gravity.CENTER);

        mProgressDialog = new ProgressDialog(this);
        //屏幕外点击无效
        //mCustomDialog.setCancelable(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在登陆......");

        //mProgressBar = (ProgressBar) findViewById(R.id.before_login);


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
                    mProgressDialog.show();
                    //登陆
                    final MyUser user = new MyUser();
                    user.setUsername(name);
                    user.setPassword(password);
                    user.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                //判断是否验证邮箱
                                if (user.getEmailVerified()) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity
                                            .class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "请前往邮箱验证", Toast
                                            .LENGTH_SHORT).show();
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

        //保存状态
        ShareUtil.putBoolean(this, "keeppass", mKeepPass.isChecked());

        //是否记住密码
        if (mKeepPass.isChecked()) {
            //记住密码
            ShareUtil.putString(this, "name", mUserName.getText().toString().trim());
            ShareUtil.putString(this, "password", mPassword.getText().toString().trim());
        } else {
            //清除密码
            ShareUtil.deleShare(this, "name");
            ShareUtil.deleShare(this, "password");
        }
        super.onDestroy();
    }

    private void setAppLabel(final TextView appLabel) {
        String cibarDailyUrl = "https://api.tianapi" +
                ".com/txapi/dictum/?key=f518734caa0bcf19f8ee1b4c4ede2b65";
        RxVolley.get(cibarDailyUrl, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    JSONArray array = jsonObject.getJSONArray("newslist");
                    JSONObject json = (JSONObject) array.get(0);
                    String content = json.getString("content");
                    appLabel.setText(content);
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
