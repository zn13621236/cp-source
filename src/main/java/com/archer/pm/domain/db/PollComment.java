package com.archer.pm.domain.db;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class PollComment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private String            guid;
    private String            pollGuid;
    private String            userGuid;
    private String            userImage;
    private String            nickName;             // user nick name..
    private long              addDate;
    private String            comment;
    private String            status;
    private String            addDateGMT;
    private int               thumbUp;
    private int               thumbDown;
    private boolean           inviteOnly;

    public boolean isInviteOnly () {
        return inviteOnly;
    }

    public void setInviteOnly (boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public String getUserImage () {
        return userImage;
    }

    public void setUserImage (String userImage) {
        this.userImage = userImage;
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public String getAddDateGMT () {
        return addDateGMT;
    }

    public void setAddDateGMT (String addDateGMT) {
        this.addDateGMT = addDateGMT;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getNickName () {
        return nickName;
    }

    public void setNickName (String nickName) {
        this.nickName = nickName;
    }

    public long getAddDate () {
        return addDate;
    }

    public void setAddDate (long addDate) {
        this.addDate = addDate;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    public String getGuid () {
        return guid;
    }

    public void setGuid (String guid) {
        this.guid = guid;
    }

    public String getPollGuid () {
        return pollGuid;
    }

    public void setPollGuid (String pollGuid) {
        this.pollGuid = pollGuid;
    }

    public int getThumbUp () {
        return thumbUp;
    }

    public void setThumbUp (int thumbUp) {
        this.thumbUp = thumbUp;
    }

    public int getThumbDown () {
        return thumbDown;
    }

    public void setThumbDown (int thumbDown) {
        this.thumbDown = thumbDown;
    }

    @Override
    public String toString () {
        return "PollComment [guid=" + guid + ", pollGuid=" + pollGuid + ", userGuid=" + userGuid + ", userImage=" + userImage + ", nickName=" + nickName + ", addDate=" + addDate + ", comment="
                + comment + ", status=" + status + ", addDateGMT=" + addDateGMT + ", thumbUp=" + thumbUp + ", thumbDown=" + thumbDown + ", inviteOnly=" + inviteOnly + "]";
    }
}
