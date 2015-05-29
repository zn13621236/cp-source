/**
 *
 */
package com.archer.pm.service.email;


import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import com.archer.pm.domain.model.EmailTemplate;
import com.archer.pm.email_template.TemplateManager;


public abstract class AbstractEmailService implements EmailService {
    private static Logger logger = LoggerFactory.getLogger(SpringEmailService.class);
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    @Qualifier("jsonTemplateManager")
    private TemplateManager templateManager;

    public void sendEmail(String template, String[] to) {
        sendEmail(templateManager.getTemplatesByGuid(template), to, null, null);
    }

    public void sendEmail(String template, String[] to, Map<String, Object> parameters) {
        sendEmail(templateManager.getTemplatesByGuid(template), to, parameters, null);
    }

//    @Override
//    public void sendEmail(String template, String[] to, Map<String, Object> parameters, Locale locale) {
//        Assert.notNull(template, "Email emailTemplate id must be not null.");
//        logger.info("The email emailTemplate is {}.", template);
//        sendEmail(templateManager.getTemplatesByGuid(template), to, parameters, locale);
//    }

    protected void sendEmail(EmailTemplate emailTemplate, String[] to, Map<String, Object> parameters, Locale locale) {
        Assert.notNull(emailTemplate, "Email emailTemplate must be not null.");
        String content = templateEngine.processTemplateIntoString(emailTemplate, parameters, locale);
        if (logger.isDebugEnabled()) {
            logger.debug(content);
        }

        doSend(emailTemplate.getFrom(), emailTemplate.getSubject(), content, to, emailTemplate.isHtml());
    }

    @Override
    public void sendEmail(final String from, final String subject, final String content, final String[] to, boolean html) {
        doSend(from, subject, content, to, html);
    }

    abstract protected void doSend(final String from, final String subject, final String content, final String[] to, final boolean html);
}
