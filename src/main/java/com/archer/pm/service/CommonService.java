package com.archer.pm.service;

import java.io.File;

import javax.servlet.http.HttpServletRequest;


public interface CommonService {

    boolean containAttributeInSession (HttpServletRequest httpServletRequest,String attribute);

    String convertTime (long time, String timeFormat);

    boolean isFileImage (File f);

    String getVoteHost ();

    String getPollHost ();

    String getTextPollPoints ();

    String getImagePollPoints ();

	long calculateTimeDifference(String time);
}
