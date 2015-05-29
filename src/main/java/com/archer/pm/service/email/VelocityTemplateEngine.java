package com.archer.pm.service.email;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.archer.pm.domain.model.EmailTemplate;


@Component
public class VelocityTemplateEngine implements TemplateEngine {
	private static Logger logger = LoggerFactory
			.getLogger(VelocityTemplateEngine.class);
	@Resource
	private VelocityEngine velocityEngine;

	@Override
	public String processTemplateIntoString(EmailTemplate emailEmailTemplate,
			Map<String, Object> model, Locale locale) {
		StringBuilder templateFileName = new StringBuilder("email/velocity/");
		templateFileName.append(emailEmailTemplate.getTemplateFile());
		if (locale != null) {
			templateFileName.append("_").append(locale.toString());
		}
		templateFileName.append(".vm");
		logger.info("The template file name is {}.",
				templateFileName.toString());
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				templateFileName.toString(), "UTF-8", model);
	}
}
