package com.HideoKuzeGits.Callback.registration;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.HideoKuzeGits.Callback.domain.RecoverPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

/**
 * Created by root on 18.02.15.
 */
@Controller
public class RecoverPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;


    @RequestMapping(value = "/recoverPassword", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String sendMessage(@RequestParam("email") String email) {

        CallbackUser user = userService.getUser(email);

        if (user == null)
            return "{\"status\": 1, \"message\": \"There is no user with such email.\"}";
        else {
            user.setRecoverPassword(new RecoverPassword());
            mailService.sendPasswordRecoverMessageToUser(user);
            return "{\"status\": 0}";
        }
    }

    @RequestMapping(value = "/recoverPassword_{recoverPasswordToken}", method = RequestMethod.GET)
    public String checkToken(@PathVariable("recoverPasswordToken") String recoverPasswordToken) {

        CallbackUser user = userService.getUserByRecoverPasswordToken(recoverPasswordToken);

        if (user != null)
            return "redirect:/landingPage?recoverPasswordTokenValid=true&recoverPasswordToken=" + recoverPasswordToken;
        else {
            return "redirect:/landingPage?recoverPasswordTokenValid=false";
        }
    }


    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String changePassword(@RequestParam(value = "token") String recoverPasswordToken,
                                 @RequestParam(value = "newPassword") String newPassword) {

        CallbackUser user = userService.getUserByRecoverPasswordToken(recoverPasswordToken);

        if (user == null)
            return "{\"status\": 1, \"message\": \"Wrong recover password token.\"}";

        user.setRecoverPassword(null);
        user.setPassword(newPassword);

        return "{\"status\": 0}";
    }


}
