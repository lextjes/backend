package alexandre.blended.service;

import alexandre.blended.exception.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine){
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Async
    public void sendMail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            Context ctx = new Context();
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            ctx.setVariables(variables);

            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(springTemplateEngine.process(template, ctx), true);

            javaMailSender.send(msg);
        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }
}
