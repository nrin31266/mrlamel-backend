package com.rin.mrlamel.common.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailProvider {
    JavaMailSender mailSender;
    @NonFinal
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(List<String> userEmails, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setTo(userEmails.toArray(new String[0]));
        helper.setFrom(fromEmail);

        mailSender.send(mimeMessage);
    }


}
