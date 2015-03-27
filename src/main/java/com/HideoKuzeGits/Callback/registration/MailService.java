package com.HideoKuzeGits.Callback.registration;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by root on 09.10.14.
 */

@Service
public class MailService {

    private String from;
    private String subject;
    private String registrateMessage;
    private String passwordRecoverMessage;

    @Autowired
    private JavaMailSender mailSender;

    @PostConstruct
    public void create() {
        try {
            InputStream messageInputStream = MailService.class.getResourceAsStream("/RegistrationMessage.txt");
            String messageJson = IOUtils.toString(messageInputStream);
            MailService mailService = new Gson().fromJson(messageJson, MailService.class);
            this.from = mailService.from;
            this.subject = mailService.subject;
            this.registrateMessage = mailService.registrateMessage;
            this.passwordRecoverMessage = mailService.passwordRecoverMessage;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendRegistrationMessageToUser(CallbackUser user) {

        String registrateMessage = this.registrateMessage.replace("[email]", user.getEmail());
        registrateMessage = registrateMessage.replace("[link]", "http://localhost:8080/approveEmail_" + user.getApproveEmailCode());
        sendMail(user, registrateMessage);
    }

    public void sendPasswordRecoverMessageToUser(CallbackUser user) {

        String recoverPasswordToken = user.getRecoverPassword().getRecoverPasswordToken();
        String passwordRecoverMessage = this.passwordRecoverMessage.replace("[link]", "http://localhost:8080/recoverPassword_" + recoverPasswordToken);
        sendMail(user, passwordRecoverMessage);
    }

    private void sendMail(CallbackUser user, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject(subject);
        mailSender.send(simpleMailMessage);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}

