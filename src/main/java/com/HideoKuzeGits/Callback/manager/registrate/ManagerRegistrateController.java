package com.HideoKuzeGits.Callback.manager.registrate;

import com.HideoKuzeGits.Callback.domain.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * Created by root on 11.12.14.
 */
@Controller
@RequestMapping(value = "/serviceManager/registrate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class ManagerRegistrateController {

    private static final String CHANGE_PASSWORD_REF = "changePassword";
    private static final String CHECK_SMS_CODE_REF = "checkSmsCode";
    private static final String SANDE_SMS_CODE_REF = "sandeSmsCode";
    private static Logger log = Logger.getLogger(ManagerRegistrateController.class.getName());

    @Autowired
    private ManagerRegistrateService registrateService;


    @RequestMapping("/getRedirectToCurrentPage")
    @ResponseBody
    public String getRedirectToCurrentPage(@RequestParam("currentPageLocation") String currentPageLocation) {

        boolean codeSend = registrateService.isCodeSend();
        boolean confirmed = registrateService.isConfirmed();


        if (currentPageLocation.endsWith(CHECK_SMS_CODE_REF))
            if (!codeSend)
                return "{\"redirect\":\"" + SANDE_SMS_CODE_REF + "\"}";
            else if (confirmed)
                return "{\"redirect\":\"" + CHANGE_PASSWORD_REF + "\"}";

        if (currentPageLocation.endsWith(CHANGE_PASSWORD_REF))
            if (!codeSend)
                return "{\"redirect\":\"" + SANDE_SMS_CODE_REF + "\"}";
            else if (!confirmed)
                return "{\"redirect\":\"" + CHECK_SMS_CODE_REF + "\"}";

        return null;

    }

    @RequestMapping("/submitCode")
    @ResponseBody
    public String sendSmsCode(@RequestParam("phone") String phone,
                              @RequestParam("registrate") boolean registrate,
                              HttpServletRequest servletRequest) {

        registrateService.configureSession(servletRequest);

        try {

            boolean managerExists = registrateService.createManager(phone);

            if ((!managerExists && registrate) || (managerExists && !registrate)) {
                registrateService.sendSmsCode();
                return "{\"status\":0, \"message\": \"Sms was send successfully.\", \"redirect\":\"" + CHECK_SMS_CODE_REF + "\"}";
            } else if (managerExists && registrate)
                return "{\"status\":2, \"message\": \"Manager with this number already exists.\", \"redirect\":\"" + CHECK_SMS_CODE_REF + "\"}";
            else if (!managerExists && !registrate)
                return "{\"status\":3, \"message\": \"There is no manager with such number.\", \"redirect\":\"" + CHECK_SMS_CODE_REF + "\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":1, \"message\": \"Error was occurs during sending sms.\"}";
        }

        return "";
    }


    @RequestMapping("/checkCode")
    @ResponseBody
    public String checkCode(@RequestParam("code") String code) {

        if (!registrateService.isCodeSend()) {

            return "{\"status\":0, \"message\": \"Code was not send.\"" +
                    ", \"redirect\":\"" + SANDE_SMS_CODE_REF + "\"}";
        }

        boolean confirmed = registrateService.checkSmsCode(code);

        if (confirmed)
            return "{\"status\":0, \"message\": \"Code was comfirmed.\", \"redirect\":\"" + CHANGE_PASSWORD_REF + "\"}";
        else
            return "{\"status\":1, \"message\": \"Wrong code.\"}";
    }


    @RequestMapping("/updateManager")
    @ResponseBody
    public String updatePassword(Manager manager, HttpSession session) {

        manager.setId(null);
        try {
            registrateService.updateManager(manager);
            if (session != null) {
                session.invalidate();
            } else {
                log.info("Session was invalidate earlier.");
            }
            return "{\"status\":0, \"message\": \"Password was successfully set.\"" +
                    ", \"redirect\":\"checkPermission\"}";

        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error was occurs during updating password");

            if (registrateService.isCodeSend())
                return "{\"status\":1, \"message\": \"Error was occurs during updating password.\"" +
                        ", \"redirect\":\"" + CHECK_SMS_CODE_REF + "\"}";
            else
                return "{\"status\":2, \"message\": \"Error was occurs during updating password.\"" +
                        ", \"redirect\":\"" + SANDE_SMS_CODE_REF + "\"}";
        }
    }

}
