package com.hultron.lifehelper.entity;

import android.widget.ImageView;

/**
 * 对话列表的实体
 */

public class ChatListData {
    //type
    private int type;
    //文本
    private String text;
    //头像
    private ImageView profile;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ImageView getProfile() {
        return profile;
    }

    public void setProfile(ImageView profile) {
        this.profile = profile;
    }
}
