package com.archer.pm.domain.db;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class Idols {

    @Id
    private String       userGuid;
    private Set <String> idolList;
    private long         modDate;

    public Idols (String userGuid) {
        super ();
        this.userGuid = userGuid;
        this.idolList = new HashSet <String> ();
        this.modDate = System.currentTimeMillis ();
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public Set <String> getIdolList () {
        return idolList;
    }

    public void setIdolList (Set <String> idolList) {
        this.idolList = idolList;
    }

    public long getModDate () {
        return modDate;
    }

    public void setModDate (long modDate) {
        this.modDate = modDate;
    }

    @Override
    public String toString () {
        return "Idols [userGuid=" + userGuid + ", idolList=" + idolList + ", modDate=" + modDate + "]";
    }
}
