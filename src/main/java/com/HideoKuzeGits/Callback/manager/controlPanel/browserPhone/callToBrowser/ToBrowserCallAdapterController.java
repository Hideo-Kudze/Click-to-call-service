package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callToBrowser;

import com.HideoKuzeGits.Callback.callback.CallsService;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.wraps.CallEndedInfo;
import com.twilio.sdk.verbs.TwiMLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.logging.Logger;

/**
 * Created by root on 15.01.15.
 */
@Controller
public class ToBrowserCallAdapterController {

    private static Logger log = Logger.getLogger(ToBrowserCallAdapterController.class.getName());

    @Autowired
    private CallsService callsService;

    @Autowired
    private ToBrowserCallService toBrowserCallService;

    //Manager pickup the phone
    public String twilioUrl(String callSid, String managerPhone, String body) throws TwiMLException {

        //Change uuid to sid
        toBrowserCallService.managerPickupThePhone(callSid, managerPhone);

        log.info("Manager pickup pone in browser. Response body " + body + ".");

        callsService.managerPickUpPhone(callSid);
        return callsService.getTwiML(callSid);
    }


    @MessageMapping(value = "/serviceManager/controlPanel/callEnded")
    public void callEnded(CallEndedInfo callEndedInfo, SimpMessageHeaderAccessor headerAccessor) {

        String reason = callEndedInfo.getReason();
        log.info("Call ended in browser phone by reason :" + reason + ".");

        String managerPhone = headerAccessor.getUser().getName();
        String uuid = toBrowserCallService.getUuid(managerPhone);

        if (uuid == null) return;

        if (reason.equals("mangerHangPhone"))
            callsService.callEnds(uuid, "busy");
        else if (reason.equals("hangupByAPI"))
            callsService.callEnds(uuid, "no-answer");
        else if (reason.equals("dontAnswer"))
            callsService.callEnds(uuid, "no-answer");
        else if (reason.equals("error"))
            callsService.callEnds(uuid, "technicalError");

        toBrowserCallService.remove(uuid);

    }


    //Call ends
    public String statusCallback(String status, String uuid, String callSid, String body) {

        log.info("Receive status callback during call to browser response from twilio pbx with body: " + body);
        toBrowserCallService.changeCallId(uuid, callSid);
        toBrowserCallService.remove(callSid);
        callsService.callEnds(callSid, status);
        return null;
    }

    public String fallback(String callSid, String errorCode, String uuid, String body) {

        log.info("Error in call to browser" + callSid + " with code: " + errorCode + "." + "Response body " + body);

        toBrowserCallService.changeCallId(uuid, callSid);
        toBrowserCallService.remove(callSid);
        callsService.callEnds(callSid, "fallback");
        return "";
    }
}
