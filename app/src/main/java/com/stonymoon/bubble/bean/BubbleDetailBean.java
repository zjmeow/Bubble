package com.stonymoon.bubble.bean;

/**
 * Created by A on 2017/11/8.
 */

public class BubbleDetailBean {

    /**
     * uid : 1
     * latitude : 26.0652
     * timestamp : Tue, 07 Nov 2017 23:30:27 -0000
     * id : 2
     * longitude : 119.209
     * content : asdasdasd
     * image : www.baidu.com
     * title : ooooo
     */

    private int uid;
    private String timestamp;
    private int id;
    private String content;
    private String image;
    private String title;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
