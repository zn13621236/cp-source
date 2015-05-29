package com.archer.pm.domain.model;

import java.io.Serializable;
import java.util.UUID;

public class Option implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String optionId;
    private String description;
	private long counter;
    
	
    public Option (String description) {
        super ();
        this.optionId=UUID.randomUUID ().toString ().replace ("-", "").substring (0,4);
        this.description = description;
        this.counter=0;
    }

    public Option () {
        super ();
        // TODO Auto-generated constructor stub
    }

    
    
    public String getOptionId () {
        return optionId;
    }

    
    public void setOptionId (String optionId) {
        this.optionId = optionId;
    }

    public String getDescription () {
        return description;
    }
    
    public void setDescription (String description) {
        this.description = description;
    }
    
    public long getCounter () {
        return counter;
    }
    
    public void setCounter (long counter) {
        this.counter = counter;
    }

    @Override
    public String toString () {
        return description;
    }
	
}
