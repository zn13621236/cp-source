package com.archer.pm.domain.db;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class PinedPolls {

    @Id
    private String       userGuid;
    private Set <String> pollSet;
    private long         modDate;

    public PinedPolls (String userGuid) {
        super ();
        this.userGuid = userGuid;
        this.pollSet = new HashSet <String> ();
        this.modDate = System.currentTimeMillis ();
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public Set <String> getPollSet () {
        return pollSet;
    }

    public void setPollSet (Set <String> pollSet) {
        this.pollSet = pollSet;
    }

    public long getModDate () {
        return modDate;
    }

    public void setModDate (long modDate) {
        this.modDate = modDate;
    }

    @Override
    public String toString () {
        return "PinedPolls [userGuid=" + userGuid + ", pollSet=" + pollSet + ", modDate=" + modDate + "]";
    }
}
