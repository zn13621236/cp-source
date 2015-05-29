package com.archer.pm.domain.db;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class Report {
    
    @Id
    private BigInteger id;
    private String pollGuid;
    private String reportReason;
    private String reporterGuid;
    private long reportTime;
    
    
    
    public BigInteger getId () {
        return id;
    }

    
    public void setId (BigInteger id) {
        this.id = id;
    }

    
    public String getReporterGuid () {
        return reporterGuid;
    }

    
    public void setReporterGuid (String reporterGuid) {
        this.reporterGuid = reporterGuid;
    }

    public String getPollGuid () {
        return pollGuid;
    }
    
    public void setPollGuid (String pollGuid) {
        this.pollGuid = pollGuid;
    }
    
    public String getReportReason () {
        return reportReason;
    }
    
    public void setReportReason (String reportReason) {
        this.reportReason = reportReason;
    }
    
    public long getReportTime () {
        return reportTime;
    }
    
    public void setReportTime (long reportTime) {
        this.reportTime = reportTime;
    }

    @Override
    public String toString () {
        return "Report [pollGuid=" + pollGuid + ", reportReason=" + reportReason + ", reportTime=" + reportTime + "]";
    }
    
    
    
}
