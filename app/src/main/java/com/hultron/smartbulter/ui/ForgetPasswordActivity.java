package com.hultron.smartbulter.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hultron.smartbulter.R;
import com.hultron.smartbulter.entity.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private Button mForgetButton;
    private EditText mEmail;
    private EditText mNowPass;
    private EditText mNewPass;
    private EditText mNewPassword;
    private Button mUpdatePass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initView();
    }


    private void initView() {
        mForgetButton = (Button) findViewById(R.id.forget_password);
        mForgetButton.setOnClickListener(this);
        mEmail = (EditText) findViewById(R.id.e_mail);
        mNowPass = (EditText) findViewById(R.id.now_pass);
        mNewPass = (EditText) findViewById(R.id.new_pass);
        mNewPassword = (EditText) findViewById(R.id.new_pass_word);
        mUpdatePass = (Button) findViewById(R.id.update_pass);
        mUpdatePass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:
                //1.获取输入框的邮箱
               final String email = mEmail.getText().toString().trim();
                //2.判断是否为空
                if (!TextUtils.isEmpty(email)) {
                    //3。发送邮件
                    MyUser.resetPasswordByEmail(email, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(ForgetPasswordActivity.this, "邮件已经发送至" + email,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "邮件发送失败", Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.update_pass:
                //1.获取输入框的值
                String now = mNowPass.getText().toString().trim();
                String newPass = mNewPass.getText().toString().trim();
                String newPassword = mNewPassword.getText().toString().trim();
                //2.判断是否为空
                if (!TextUtils.isEmpty(now) &&
                        !TextUtils.isEmpty(newPass) &&
                        !TextUtils.isEmpty(newPassword)) {
                    //3.判断两次新密码是否一致
                    if (newPass.equals(newPassword)) {
                        //4.重置密码
                        MyUser.updateCurrentUserPassword(now, newPass, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(ForgetPasswordActivity.this, "重置密码成功。", Toast
                                            .LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "重置密码失败，请重新尝试",
                                            Toast
                                            .LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
