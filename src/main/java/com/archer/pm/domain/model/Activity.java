package com.archer.pm.domain.model;

import com.archer.pm.domain.db.Poll;

public class Activity {

    private String type;
    private String guid;
    private String time;
    private String nickName;
    private String category;
    private String userImage;
    private Poll   poll;

    public Activity (String type, String guid, String time, String nickName, String category) {
        super ();
        this.type = type;
        this.guid = guid;
        this.time = time;
        this.nickName = nickName;
        this.category = category;
    }

    public Activity (String type, String guid, String time, String nickName) {
        super ();
        this.type = type;
        this.guid = guid;
        this.time = time;
        this.nickName = nickName;
    }

    public Activity (String type, String guid, String time, String nickName, String category, String userImage) {
        super ();
        this.type = type;
        this.guid = guid;
        this.time = time;
        this.nickName = nickName;
        this.category = category;
        this.userImage = userImage;
    }

    public Poll getPoll () {
        return poll;
    }

    public void setPoll (Poll poll) {
        this.poll = poll;
    }

    public String getUserImage () {
        return userImage;
    }

    public void setUserImage (String userImage) {
        this.userImage = userImage;
    }

    public String getCategory () {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public String getTime () {
        return time;
    }

    public void setTime (String time) {
        this.time = time;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getGuid () {
        return guid;
    }

    public void setGuid (String guid) {
        this.guid = guid;
    }

    public String getNickName () {
        return nickName;
    }

    public void setNickName (String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString () {
        return "Activity [type=" + type + ", guid=" + guid + ", time=" + time + ", nickName=" + nickName + ", category=" + category + ", userImage=" + userImage + ", poll=" + poll + "]";
    }
}
