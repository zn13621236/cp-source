package com.archer.pm.service.email;

import java.util.Map;


public interface EmailService {

    public void sendEmail(String template, String[] to);

    public void sendEmail(String template, String[] to, Map<String, Object> parameters);

    public void sendEmail(String from, String subject, String content, final String[] to, boolean html);

}
