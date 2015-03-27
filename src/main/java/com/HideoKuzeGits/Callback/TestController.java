package com.HideoKuzeGits.Callback;

import com.HideoKuzeGits.Callback.registration.ScheduledUserCleaner;
import com.twilio.sdk.verbs.TwiMLException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by root on 13.02.15.
 */

@Controller
public class TestController implements ApplicationContextAware {


    @Autowired
    private ScheduledUserCleaner scheduledUserCleaner;
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/twilio/test", method = RequestMethod.POST, produces = "text/html")
    @ResponseBody
    public String test(@RequestParam("callSid") String callSid) throws TwiMLException {

        scheduledUserCleaner.removeOldRegistrationRequests();

        return "";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
