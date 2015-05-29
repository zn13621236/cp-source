package com.archer.pm.domain.db;

import java.io.Serializable;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class PollVoterMetaData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private String            pollGuid;
    private Set <String>      voterIp;
    private Set <String>      voterGuid;

    public String getPollGuid () {
        return pollGuid;
    }

    public void setPollGuid (String pollGuid) {
        this.pollGuid = pollGuid;
    }

    public Set <String> getVoterIp () {
        return voterIp;
    }

    public void setVoterIp (Set <String> voterIp) {
        this.voterIp = voterIp;
    }

    public Set <String> getVoterGuid () {
        return voterGuid;
    }

    public void setVoterGuid (Set <String> voterGuid) {
        this.voterGuid = voterGuid;
    }

    @Override
    public String toString () {
        return "PollVoterMetaData [pollGuid=" + pollGuid + ", voterIp=" + voterIp + ", voterGuid=" + voterGuid + "]";
    }
}
