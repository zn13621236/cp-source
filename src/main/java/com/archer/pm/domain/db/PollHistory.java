package com.archer.pm.domain.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class PollHistory {

    /**
     * 
     */
    @Id
    private String        phId;
    private String        userGuid;
    private List <String> pollList = new ArrayList <String> ();

    public String getPhId () {
        return phId;
    }

    public void setPhId (String phId) {
        this.phId = phId;
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public List <String> getPollList () {
        return pollList;
    }

    public void setPollList (List <String> pollList) {
        this.pollList = pollList;
    }

	@Override
	public String toString() {
		return "PollHistory [phId=" + phId + ", userGuid=" + userGuid
				+ ", pollList=" + pollList + "]";
	}
    
}
