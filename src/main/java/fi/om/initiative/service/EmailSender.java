package fi.om.initiative.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void send(EmailHelper emailHelper, JavaMailSender javaMailSender) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(emailHelper.to);
        helper.setFrom(emailHelper.from); //to avoid spam filters
        helper.setReplyTo(emailHelper.replyTo);
        helper.setSubject(emailHelper.subject);
        helper.setText(emailHelper.text, emailHelper.html);

        javaMailSender.send(message);

    }
}
