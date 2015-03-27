package com.HideoKuzeGits.Callback.manager.registrate;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.twilio.sdk.TwilioRestException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Created by root on 11.12.14.
 */
@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ManagerRegistrateService {


    private static Logger log = Logger.getLogger(ManagerRegistrateService.class.getName());
    private Manager manager;
    @Autowired
    @Qualifier("twilioService")
    private TwilioService twilioService;
    @PersistenceContext
    private EntityManager em;
    private String managerId;
    private String code;
    private boolean confirmed = false;
    private int sessionTtlMinutes = 15;
    private int sessionInactiveIntervalMinutes = 5;


    public void configureSession(HttpServletRequest servletRequest) {

        final HttpSession session = servletRequest.getSession();
        session.setMaxInactiveInterval(60 * sessionInactiveIntervalMinutes);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                session.invalidate();
                log.info("Sessions expire due timeout.");
            }
        }, 1000 * 60 * sessionTtlMinutes);
    }

    public void sendSmsCode() throws IOException, TemplateException, TwilioRestException {

        String phoneNumber = manager.getPhoneNumber();
        managerId = manager.getId();
        confirmed = false;
        code = RandomStringUtils.random(5, false, true);

        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File("/"));
        URL path = getClass().getResource("/managerRegistrateCodeSms.ftl");
        Template template = cfg.getTemplate(path.getFile());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("code", code);
        StringWriter stringWriter = new StringWriter();
        template.process(data, stringWriter);
        String message = stringWriter.toString();
        twilioService.sendSMS(phoneNumber, message);

    }


    public boolean createManager(String managerPhone) {

        try {
            TypedQuery<Manager> query
                    = em.createQuery("SELECT m FROM Manager m WHERE m.phoneNumber = :phone", Manager.class);
            query.setParameter("phone", managerPhone);
            this.manager = query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            Manager manager = new Manager();
            manager.setPhoneNumber(managerPhone);
            this.manager = manager;
            return false;
        }

    }


    public boolean checkSmsCode(String receivedCode) {

        confirmed = code.equals(receivedCode);
        code = null;

        if (confirmed)
            log.info("Manager phone was confirmed.");
        else
            log.info("Manager phone was not confirmed.");

        return confirmed;
    }

    @Transactional
    public void updateManager(Manager newManagerParameters) throws Exception {

        if (!confirmed) {
            log.info("Can`t change password because phone was no confirmed.");
            throw new RegistrateException("Manager phone was not confirmed");
        }

        UsefulStaticMethods.<Manager>merge(manager, newManagerParameters);
        em.merge(manager);

        log.info("Password was successfully changed.");
    }

    public boolean isCodeSend() {
        return code != null;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

}
