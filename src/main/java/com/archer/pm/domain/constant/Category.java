package com.archer.pm.domain.constant;

public enum Category {
//    IGNORE1ST("IGNORE FIRST"),
    SPORTS("sports"),
    OTHERS("others"),
    ENTERTAINMENT("entertainment"),
    MUSIC("music"),
    MOVIE("movie"),
    CARS("cars"),
    PRODUCT("product"),
    POLITICS("politics"),
    TECHNOLOGY("technology"),
    INDUSTRY("industry"),
//    IGNORELAST("IGNORE LAST"),
    ;
    
    
    private final String text;
    private Category(final String text){ 
        this .text=text;
    }
    public String getText () {
        return text;
    }
}
