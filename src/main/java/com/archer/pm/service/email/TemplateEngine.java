/**
 *
 */
package com.archer.pm.service.email;


import java.util.Locale;
import java.util.Map;

import com.archer.pm.domain.model.EmailTemplate;


public interface TemplateEngine {
    /**
     * @param emailEmailTemplate
     * @param model
     * @return
     */
    public String processTemplateIntoString(EmailTemplate emailEmailTemplate, Map<String, Object> model, Locale locale);
}
