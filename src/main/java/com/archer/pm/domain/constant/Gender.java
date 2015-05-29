package com.archer.pm.domain.constant;

public enum Gender {  
    MALE("male"),
    FEMALE("female"),
    NOT_SPECIFIED("not specified"),
    ;

    private final String gender;
    private Gender(final String gender){ 
        this .gender=gender;
    }
    public String getGender () {
        return gender;
    }
}
