package com.archer.pm.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.archer.pm.domain.constant.Category;


public class ImagePollEntity {

    private String question;
    private MultipartFile file1;
    private MultipartFile file2;
    private String          gender       = "all";
    private String          ageRange     = "all";
    private List <Category> categoryList = new ArrayList <Category> ();
    
    public String getQuestion () {
        return question;
    }
    
    public void setQuestion (String question) {
        this.question = question;
    }
    
    public MultipartFile getFile1 () {
        return file1;
    }
    
    public void setFile1 (MultipartFile file1) {
        this.file1 = file1;
    }
    
    public MultipartFile getFile2 () {
        return file2;
    }
    
    public void setFile2 (MultipartFile file2) {
        this.file2 = file2;
    }
    
    public String getGender () {
        return gender;
    }
    
    public void setGender (String gender) {
        this.gender = gender;
    }
    
    public String getAgeRange () {
        return ageRange;
    }
    
    public void setAgeRange (String ageRange) {
        this.ageRange = ageRange;
    }
    
    public List <Category> getCategoryList () {
        return categoryList;
    }
    
    public void setCategoryList (List <Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString () {
        return "ImagePollEntity [question=" + question + ", file1=" + file1 + ", file2=" + file2 + ", gender=" + gender + ", ageRange=" + ageRange + ", categoryList=" + categoryList + "]";
    }

}
