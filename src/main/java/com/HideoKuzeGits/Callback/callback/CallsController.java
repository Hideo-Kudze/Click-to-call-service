package com.HideoKuzeGits.Callback.callback;

import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.verbs.TwiMLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by root on 15.10.14.
 */

@Controller
@RequestMapping(method = RequestMethod.POST)
public class CallsController {


    public static final String NUMBER_OF_DIGITS_IN_NUMBER = "5";
    private static Logger log = Logger.getLogger(CallsController.class.getName());
    @Autowired
    private CallsService callsService;

    public static void isPhoneNumberValid(String phoneNumber) throws IncorrectNumberException {
        if (!phoneNumber.matches("(\\+?)(\\d{" + NUMBER_OF_DIGITS_IN_NUMBER + ",})")) {
            throw new IncorrectNumberException("Invalid phone number");
        }
    }

    @RequestMapping(value = "call")
    @ResponseBody
    public String makeCall(@RequestParam("clientNumber") String clientNumber,
                           @RequestParam("siteId") String siteId,
                           HttpServletResponse response) throws TwilioRestException, UnknownHostException, IncorrectNumberException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        isPhoneNumberValid(clientNumber);
        callsService.makeCall(siteId, clientNumber);
        return "";
    }

    @RequestMapping(value = "twilio/url", produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String managerPickUpPhone(@RequestParam("CallSid") String callSid,
                                     @RequestBody String body) throws TwilioRestException {
        log.info("Receive callback response from twilio pbx with body: " + body);
        callsService.managerPickUpPhone(callSid);
        return callsService.getTwiML(callSid);
    }


    @RequestMapping(value = "twilio/statusCallback", produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String statusCallback(@RequestParam("CallStatus") String status,
                                 @RequestParam("CallSid") String callSid,
                                 @RequestBody String body) throws FileNotFoundException, TwiMLException {


        log.info("Receive status callback response from twilio pbx with body: " + body);
        callsService.callEnds(callSid, status);
        return null;
    }

    @RequestMapping(value = "/twilio/fallback",
            method = RequestMethod.POST,
            produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String fallback(@RequestParam("CallSid") String callSid,
                           @RequestParam("ErrorCode") String errorCode,
                           @RequestBody String body) {

        callsService.callEnds(callSid, "fallback");
        return "";
    }


}

