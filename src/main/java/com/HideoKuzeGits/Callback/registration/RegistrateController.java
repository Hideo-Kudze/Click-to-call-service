package com.HideoKuzeGits.Callback.registration;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by root on 07.10.14.
 */
@Controller
public class RegistrateController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/registrate", method = RequestMethod.GET)
    public String registrate(ModelMap modelMap) {

        return "/WEB-INF/registrate/registrate.jsp";
    }

    @RequestMapping(value = "/registrate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String saveUser(CallbackUser callbackUser) {


        try {
            userService.saveUser(callbackUser);
        } catch (RegistrationException registrationException) {
            String message = registrationException.getMessage();
            int status = registrationException.getCode();
            return "{\"status\": " + status + ", \"message\": \"" + message + "\"}";
        }
        mailService.sendRegistrationMessageToUser(callbackUser);
        return "{\"status\": 0}";
    }

    @RequestMapping(value = "/approveEmail_{approveEmailCode}", method = RequestMethod.GET)
    public String approveEmail(ModelMap modelMap, @PathVariable(value = "approveEmailCode") String uuid) {

        boolean emailConfirmed = true;

        try {
            userService.approveEmail(uuid);
        } catch (RegistrationException e) {
            emailConfirmed = false;
        }

        modelMap.put("emailConfirmed", emailConfirmed);
        return "redirect:/landingPage?emailConfirmed=" + emailConfirmed;
    }


}
