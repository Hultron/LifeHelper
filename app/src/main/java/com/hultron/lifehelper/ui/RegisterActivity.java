package com.hultron.lifehelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hultron.lifehelper.R;
import com.hultron.lifehelper.entity.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/*
* 注册界面
* */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText mUser, mAge, mDesc, mPass, mPassword, mEmail;
    private RadioGroup mRadioGroup;
    Button mRegister;
    private boolean isGender = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        mUser = (EditText) findViewById(R.id.et_user);
        mAge = (EditText) findViewById(R.id.et_age);
        mDesc = (EditText) findViewById(R.id.et_desc);
        mPass = (EditText) findViewById(R.id.et_pass);
        mPassword = (EditText) findViewById(R.id.et_repass);
        mEmail = (EditText) findViewById(R.id.et_email);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRegister = (Button) findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                //获取输入框的值
                String name = mUser.getText().toString().trim();
                String age = mAge.getText().toString().trim();
                String desc = mDesc.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                //判断是否为空
                if (!TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(age) &&
                        !TextUtils.isEmpty(pass) &&
                        !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(email)) {

                    //判断两次输入的密码是否一致
                    if (pass.equals(password)) {
                        //判断性别
                        mRadioGroup.setOnCheckedChangeListener(
                                new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        if (checkedId == R.id.rb_boy) {
                                            isGender = true;
                                        } else if (checkedId == R.id.rb_girl) {
                                            isGender = false;
                                        }
                                    }
                                });

                        //判断简介是否为空
                        if (TextUtils.isEmpty(desc)) {
                            desc = getString(R.string.desc_content);
                        }

                        //注册
                        MyUser user = new MyUser();
                        user.setUsername(name);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.setAge(Integer.parseInt(age));
                        user.setSex(isGender);
                        user.setDesc(desc);

                        user.signUp(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast
                                            .LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "注册失败" + e.toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
