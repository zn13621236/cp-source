/**
 *
 */
package com.archer.pm.service.email;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class SpringEmailService extends AbstractEmailService {
    private static Logger logger = LoggerFactory.getLogger(SpringEmailService.class);
    @Resource
    private JavaMailSender mailSender;

    @Override
    protected void doSend(final String from, final String subject, final String content, final String[] to,
                          final boolean html) {
        Assert.notNull(from, "The from address must be not null.");
        Assert.notNull(subject, "The subject must be not null.");
        Assert.notNull(to, "The to address must be not null.");
        if (logger.isInfoEnabled()) {
            logger.info("The email's subject is {} which is from {}.", new String[]{subject, from});
        }
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setSubject(subject);
                message.setTo(to);
                message.setFrom(from);

                if (logger.isDebugEnabled()) {
                    logger.debug(content);
                }

                message.setText(content, html);
            }
        };

        mailSender.send(preparator);
    }

}
