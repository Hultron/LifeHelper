package com.hultron.smartbulter.entity;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

    private int age;
    private boolean sex; //true 为男， false 为女
    private String desc;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
