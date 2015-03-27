package com.HideoKuzeGits.Callback.manager.controlPanel.callFromBrowser.browserPhone;

import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.manager.ManagerDAO;
import com.HideoKuzeGits.Callback.registration.ActiveUser;
import com.twilio.sdk.client.TwilioCapability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by root on 18.12.14.
 */
@Controller
@Scope(value = "session")
public class BrowserPhoneController {

    @Autowired
    @Qualifier("twilioService")
    private TwilioService twilio;

    @Autowired
    private ManagerDAO managerDAO;

    private static Logger log = Logger.getLogger(BrowserPhoneController.class.getName());

    @RequestMapping(value = "/serviceManager/controlPanel/callFromBrowser")
    @Transactional
    public String callFromBrowser(@ActiveUser User user, ModelMap modelMap) {

        String managerPhone = user.getUsername();

        try {
            modelMap.put("token", twilio.generateToken(managerPhone));
            Manager manager = managerDAO.getManagerByPhone(managerPhone);
            List<ServicedSite> servicedSites = manager.getServicedSites();
            servicedSites.isEmpty();
            modelMap.put("manager", manager);
        } catch (TwilioCapability.DomainException e) {
            e.printStackTrace();
        }

        log.info("Return browserPhone for manager with phone" + managerPhone + ".");
        return "serviceManager/controlPanel/browser-phone";

    }


}
