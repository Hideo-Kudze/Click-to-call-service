package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callFromBrowser;

import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callToBrowser.ToBrowserCallAdapterController;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callToBrowser.ToBrowserCallService;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.verbs.TwiMLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Created by root on 10.12.14.
 */
@Controller
public class FromBrowserCallController {

    private static Logger log = Logger.getLogger(FromBrowserCallController.class.getName());

    @Autowired
    private FromBrowserCallService fromBrowserCallService;

    @Autowired
    private ToBrowserCallAdapterController toBrowserCallAdatapter;

    @Autowired
    private ToBrowserCallService toBrowserCallService;

    @Autowired
    private TwilioService twilioService;

    @RequestMapping(value = "/twilio/callFromBrowser/url",
            method = RequestMethod.POST,
            produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String twilioUrl(@RequestParam("CallSid") String callSid,
                            @RequestParam(value = "tocall", required = false) String clientNumber,
                            @RequestParam("managerPhone") String managerPhone,
                            @RequestBody String body) throws TwiMLException {

        if (clientNumber == null)
            return toBrowserCallAdatapter.twilioUrl(callSid, managerPhone, body);


        log.info("Manager with number " + managerPhone + " call from browser to number "
                + clientNumber + ".");
        fromBrowserCallService.callBegins(callSid, clientNumber, managerPhone);
        return fromBrowserCallService.getTwiMl(callSid);
    }


    @RequestMapping(value = "/twilio/callFromBrowser/statusCallback",
            method = RequestMethod.POST,
            produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String statusCallback(@RequestParam("CallSid") String callSid,
                                 @RequestParam("CallDuration") String duration,
                                 @RequestParam("CallStatus") String status,
                                 @RequestParam("Caller") String clientId,
                                 @RequestBody String body) throws TwiMLException {

        String managerNumber = clientId.replace("client:", "");
        String uuid = toBrowserCallService.getUuid(managerNumber);

        if (uuid != null)
            return toBrowserCallAdatapter.statusCallback(status, uuid, callSid, body);


        log.info("Call + " + callSid + " + ends. Duration:  " + duration + ".");
        fromBrowserCallService.callEnds(callSid, duration);
        return "";
    }


    @RequestMapping(value = "/twilio/callFromBrowser/fallback",
            method = RequestMethod.POST,
            produces = "text/xml; charset=utf-8")
    @ResponseBody
    public String fallback(@RequestParam("CallSid") String callSid,
                           @RequestParam("ErrorCode") String errorCode,
                           @RequestParam("managerPhone") String managerPhone,
                           @RequestBody String body) {


        String uuid = toBrowserCallService.getUuid(managerPhone);
        if (uuid != null)
            return toBrowserCallAdatapter.fallback(callSid, errorCode, uuid, body);

        Call call = twilioService.getCallToManager(callSid);
        String duration = call.getDuration();

        fromBrowserCallService.callEnds(callSid, duration);
        log.info("Error in call from browser" + callSid + " with code: " + errorCode + "." + "Response body " + body);
        return callSid;
    }

}
