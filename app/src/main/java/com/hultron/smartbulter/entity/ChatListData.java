package com.hultron.smartbulter.entity;

/**
 * 对话列表的实体
 */

public class ChatListData {
    //type
    private int type;
    //文本
    private String text;

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
}
