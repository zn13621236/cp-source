package com.archer.pm.domain.db;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class User implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private String            guid;
    @Email
    @NotNull
    private String            userName;
    @Size (min = 5, max = 40)
    private String            passWord;
    private long              points;
    private String            status;
    private long              createDate;
    private String            gender           = "not specified";
    private String            birthYear        = "not specified";
    private List <String>     interestList;
    private Boolean           acceptTerm       = false;
    private String            nickName;
    private String            userImage;
    private int               fanNum;
    private int               level;
    private long              expLimit;
    private long              exp;
    private long              lastLogInTime;
    private long              logInTime;

    public int getFanNum () {
        return fanNum;
    }

    public void setFanNum (int fanNum) {
        this.fanNum = fanNum;
    }

    public String getUserImage () {
        return userImage;
    }

    public void setUserImage (String userImage) {
        this.userImage = userImage;
    }

    public String getNickName () {
        return nickName;
    }

    public void setNickName (String nickName) {
        this.nickName = nickName;
    }

    public String getGuid () {
        return guid;
    }

    public void setGuid (String guid) {
        this.guid = guid;
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    public String getPassWord () {
        return passWord;
    }

    public void setPassWord (String passWord) {
        this.passWord = passWord;
    }

    public long getPoints () {
        return points;
    }

    public void setPoints (long points) {
        this.points = points;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public long getCreateDate () {
        return createDate;
    }

    public void setCreateDate (long createDate) {
        this.createDate = createDate;
    }

    public String getGender () {
        return gender;
    }

    public void setGender (String gender) {
        this.gender = gender;
    }

    public String getBirthYear () {
        return birthYear;
    }

    public void setBirthYear (String birthYear) {
        this.birthYear = birthYear;
    }

    public Boolean getAcceptTerm () {
        return acceptTerm;
    }

    public void setAcceptTerm (Boolean acceptTerm) {
        this.acceptTerm = acceptTerm;
    }

    public List <String> getInterestList () {
        return interestList;
    }

    public void setInterestList (List <String> interestList) {
        this.interestList = interestList;
    }

    public int getLevel () {
        return level;
    }

    public void setLevel (int level) {
        this.level = level;
    }

    public long getExpLimit () {
        return expLimit;
    }

    public void setExpLimit (long expLimit) {
        this.expLimit = expLimit;
    }

    public long getExp () {
        return exp;
    }

    public void setExp (long exp) {
        this.exp = exp;
    }

    public long getLastLogInTime () {
        return lastLogInTime;
    }

    public void setLastLogInTime (long lastLogInTime) {
        this.lastLogInTime = lastLogInTime;
    }

    public long getLogInTime () {
        return logInTime;
    }

    public void setLogInTime (long logInTime) {
        this.logInTime = logInTime;
    }

    @Override
    public String toString () {
        return "User [guid=" + guid + ", userName=" + userName + ", passWord=" + passWord + ", points=" + points + ", status=" + status + ", createDate=" + createDate + ", gender=" + gender
                + ", birthYear=" + birthYear + ", interestList=" + interestList + ", acceptTerm=" + acceptTerm + ", nickName=" + nickName + ", userImage=" + userImage + ", fanNum=" + fanNum
                + ", level=" + level + ", expLimit=" + expLimit + ", exp=" + exp + ", lastLogInTime=" + lastLogInTime + ", logInTime=" + logInTime + "]";
    }
}
