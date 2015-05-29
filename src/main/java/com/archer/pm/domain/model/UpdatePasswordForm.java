package com.archer.pm.domain.model;

import java.math.BigInteger;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import com.archer.pm.domain.db.User;


public class UpdatePasswordForm {

    @Id
    private BigInteger    id;
    private String        userName;
    @Size (min = 5, max = 40)
    private String        oldPassWord;
    @Size (min = 5, max = 40)
    private String        newPassWord;
    private String        userImage;
    private long          points;
    private String        status;
    private long          createDate;
    private String        gender     = "not specified";
    private String        birthYear  = "not specified";
    private List <String> interestList;
    private Boolean       acceptTerm = false;

    public UpdatePasswordForm (User user) {
        super ();
        this.userName = user.getUserName ();
        this.oldPassWord = user.getPassWord ();
        this.points = user.getPoints ();
        this.status = user.getStatus ();
        this.createDate = user.getCreateDate ();
        this.gender = user.getGender ();
        this.birthYear = user.getBirthYear ();
        this.interestList = user.getInterestList ();
        this.acceptTerm = user.getAcceptTerm ();
        this.userImage = user.getUserImage ();
    }

    public UpdatePasswordForm () {
        super ();
        // TODO Auto-generated constructor stub
    }

    public String getUserImage () {
        return userImage;
    }

    public void setUserImage (String userImage) {
        this.userImage = userImage;
    }

    public BigInteger getId () {
        return id;
    }

    public void setId (BigInteger id) {
        this.id = id;
    }

    public String getOldPassWord () {
        return oldPassWord;
    }

    public void setOldPassWord (String oldPassWord) {
        this.oldPassWord = oldPassWord;
    }

    public String getNewPassWord () {
        return newPassWord;
    }

    public void setNewPassWord (String newPassWord) {
        this.newPassWord = newPassWord;
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    public String getPassWord () {
        return oldPassWord;
    }

    public void setPassWord (String passWord) {
        this.oldPassWord = passWord;
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

    public List <String> getInterestList () {
        return interestList;
    }

    public void setInterestList (List <String> interestList) {
        this.interestList = interestList;
    }

    public Boolean getAcceptTerm () {
        return acceptTerm;
    }

    public void setAcceptTerm (Boolean acceptTerm) {
        this.acceptTerm = acceptTerm;
    }

    @Override
    public String toString () {
        return "UpdatePasswordForm [id=" + id + ", userName=" + userName + ", oldPassWord=" + oldPassWord + ", newPassWord=" + newPassWord + ", userImage=" + userImage + ", points=" + points
                + ", status=" + status + ", createDate=" + createDate + ", gender=" + gender + ", birthYear=" + birthYear + ", interestList=" + interestList + ", acceptTerm=" + acceptTerm + "]";
    }
}
