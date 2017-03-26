package com.hultron.smartbulter.entity;

/**
 * 时事新闻数据类
 */

public class NewsData {
    //标题
    private String title;
    //来源
    private String src;
    //图片的url
    private String imgUrl;
    //新闻地址
    private String newsUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
}
