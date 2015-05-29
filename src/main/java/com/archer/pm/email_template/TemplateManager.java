package com.archer.pm.email_template;



import java.util.Collection;

import com.archer.pm.domain.model.EmailTemplate;

public interface TemplateManager {
    EmailTemplate getTemplatesByGuid(String guid);

    /**
     * @return
     */
    Collection<EmailTemplate> getEmailTemplates();
}
