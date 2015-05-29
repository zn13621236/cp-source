package com.archer.pm.email_template;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import com.archer.pm.domain.model.EmailTemplate;


public abstract class AbstractTemplateManager implements TemplateManager, InitializingBean {
    private final Map<String, EmailTemplate> templateMap = new HashMap<>();
    private List<EmailTemplate> emailTemplates;

    @Override
    public EmailTemplate getTemplatesByGuid(String guid) {
        return templateMap.get(guid);
    }

    @Override
    public Collection<EmailTemplate> getEmailTemplates() {
        return emailTemplates;
    }

    protected abstract List<EmailTemplate> loadTemplate() throws IOException;

    @Override
    public void afterPropertiesSet() throws Exception {
        emailTemplates = loadTemplate();
        if (!CollectionUtils.isEmpty(emailTemplates)) {
            for (EmailTemplate emailTemplate : emailTemplates) {
                templateMap.put(emailTemplate.getGuid(), emailTemplate);
            }
        }
    }
}
