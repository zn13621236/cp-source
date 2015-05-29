/**
 *
 */
package com.archer.pm.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class CommonServiceImpl implements CommonService {
    private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Value("${app.host.vote}")
    private String voteHost;
    @Value("${app.host.poll}")
    private String pollHost;
    @Value("${poll.points.text}")
    private String textPoints;
    @Value("${poll.points.image}")
    private String imagePoints;
    
    @Override
    public String getVoteHost(){
        return voteHost;
    }
    
    @Override
    public String getPollHost(){
        return pollHost;
    }
    
    @Override
    public String getTextPollPoints(){
        return textPoints;
    }
    
    @Override
    public String getImagePollPoints(){
        return imagePoints;
    }
    
    @SuppressWarnings ("unchecked")
    @Override
    public boolean containAttributeInSession (HttpServletRequest httpServletRequest,String attribute) {
        Enumeration<String> e=httpServletRequest.getSession ().getAttributeNames ();
        while(e.hasMoreElements ()){
        if(e.nextElement ().toString ().contains (attribute)){
            return true;
        }
        }
        return false;
    }
   
    @Override
    public String convertTime(long time,String timeFormat){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        DateFormat formatter = new SimpleDateFormat(timeFormat);
        return formatter.format(calendar.getTime());
    }
    
    @Override
    public long calculateTimeDifference(String time){
        Calendar calendar = Calendar.getInstance();
    	   calendar.setTimeInMillis(System.currentTimeMillis());
           DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
           String currentTime= formatter.format(calendar.getTime());
           Date d1 = null;
   		Date d2 = null;
   		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
   		long diffDays=0;
   		try {
   			d1 = format.parse(time);
   			d2 = format.parse(currentTime);
    
   			//in milliseconds
   			long diff = d2.getTime() - d1.getTime();
    
//   			long diffSeconds = diff / 1000 % 60;
//   			long diffMinutes = diff / (60 * 1000) % 60;
//   			long diffHours = diff / (60 * 60 * 1000) % 24;
   			 diffDays = diff / (24 * 60 * 60 * 1000);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return diffDays;
    }
    
    @Override
    public boolean isFileImage(File f){
        String mimetype= new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        if(type.equals("image"))
           return true;
        else 
            return false;
    }
    
}
