package com.archer.pm.domain.model;

import java.io.Serializable;

public class PollProfile implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            gender;
    private int               minAge;
    private int               maxAge;

    public PollProfile (String gender, int minAge, int maxAge) {
        super ();
        this.gender = gender;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public PollProfile () {
        super ();
        // TODO Auto-generated constructor stub
    }

    public String getGender () {
        return gender;
    }

    public void setGender (String gender) {
        this.gender = gender;
    }

    public int getMinAge () {
        return minAge;
    }

    public void setMinAge (int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge () {
        return maxAge;
    }

    public void setMaxAge (int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public String toString () {
        return "PollProfile [gender=" + gender + ", minAge=" + minAge + ", maxAge=" + maxAge + "]";
    }
}
