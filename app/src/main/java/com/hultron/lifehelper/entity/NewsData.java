package com.hultron.lifehelper.entity;

/**
 * 时事新闻数据类
 */

public class NewsData {
    //标题
    private String title;

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    //新闻时间
    private String ctime;

    //新闻地址
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
