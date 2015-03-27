package com.HideoKuzeGits.Callback.logs;

import com.HideoKuzeGits.Callback.callback.CallFromAllManagers;
import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.domain.CallLog;
import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callFromBrowser.FromBrowserCall;
import com.twilio.sdk.resource.instance.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by root on 03.12.14.
 */
@Service
public class CallLogFactory {

    private static Logger log = Logger.getLogger(CallLogFactory.class.getName());


    @Autowired
    @Qualifier("twilioService")
    private TwilioService twilioService;


    public CallLog createCallLog(CallFromAllManagers callFromAllManagers) {

        log.info("Create callFromBrowser log for callFromBrowser with sid: " + callFromAllManagers.getSid());
        String status = callFromAllManagers.getStatus();

        if (status.equals("completed") || status.equals("fallback"))
            return createManagerPickUpPhoneLog(callFromAllManagers);
        else if (status.equals("technicalError"))
            technicalError(callFromAllManagers.getClientNumber(), callFromAllManagers.getSite());
        else if (callFromAllManagers.isAllCallsFromGroupEnds() && !status.equals("completed"))
            return noOneManagerPickUpThePhone(callFromAllManagers);

        return null;
    }

    public CallLog createManagerPickUpPhoneLog(CallFromAllManagers serviceCall) {

        CallbackUser owner = serviceCall.getSite().getOwner();
        String callSid = serviceCall.getSid();
        String domain = serviceCall.getSite().getDomain();
        Call twilioCallToManager = getCallToManager(callSid);
        Call twilioCallToClient = twilioService.getCallToClient(callSid);
        Long time = serviceCall.getBeginTime();
        String managerNumber = twilioCallToManager.getTo();
        String duration = twilioCallToManager.getDuration();
        String clientNumber = serviceCall.getClientNumber();

        CallLog callLog = new CallLog();
        callLog.setSid(callSid);
        callLog.setOwner(owner);
        callLog.setDomain(domain);
        callLog.setTime(new Date(time));
        callLog.setManagerNumber(managerNumber);
        callLog.setDuration(Integer.valueOf(duration));
        callLog.setClientNumber(clientNumber);

        String callToClientStatus = null;
        if (twilioCallToClient != null)
            callToClientStatus = twilioCallToClient.getStatus();

        if (serviceCall.getStatus().equals("fallback") ||
                callToClientStatus.equals("failed")) {
            callLog.setStatus(CallLog.Status.TECHNICAL_ERROR);
            return callLog;
        } else if (serviceCall.isStoppedDueToLackOfMoney()) {
            callLog.setStatus(CallLog.Status.NOT_ENOUGH_MONEY);
            return callLog;
        } else if (serviceCall.isStopped()) {
            callLog.setStatus(CallLog.Status.ANOTHER_MANGER_TOOK_CALL);
            return callLog;
        } else if (twilioCallToClient == null
                || callToClientStatus.equals("busy")
                || callToClientStatus.equals("no-answer")) {
            callLog.setStatus(CallLog.Status.DISCONNECTED);
            return callLog;
        } else if (callToClientStatus.equals("completed")) {
            callLog.setStatus(CallLog.Status.SUCCESSFUL);
            return callLog;
        }

        throw new NoSuitableStatusException();
    }

    private Call getCallToManager(String callSid) {
        Call twilioCallToManager = null;

        for (int i = 0; i < 14; i++) {
            twilioCallToManager = twilioService.getCallToManager(callSid);

            if (twilioCallToManager.getDuration() != null)
                return twilioCallToManager;

            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return twilioCallToManager;
    }

    public CallLog allManagersBusy(String clientNumber, ServicedSite site) {

        CallLog callLog = failedCall(clientNumber, site);
        callLog.setStatus(CallLog.Status.ALL_MANAGERS_ARE_BUSY);
        return callLog;
    }


    public CallLog notEnoughMoney(String clientNumber, ServicedSite site) {

        CallLog callLog = failedCall(clientNumber, site);
        callLog.setStatus(CallLog.Status.NOT_ENOUGH_MONEY);
        return callLog;
    }

    public CallLog technicalError(String clientNumber, ServicedSite site) {

        CallLog callLog = failedCall(clientNumber, site);
        callLog.setStatus(CallLog.Status.TECHNICAL_ERROR);
        return callLog;
    }


    private CallLog failedCall(String clientNumber, ServicedSite site) {

        CallbackUser owner = site.getOwner();
        String domain = site.getDomain();
        Date time = new Date(System.currentTimeMillis());

        CallLog callLog = new CallLog();
        callLog.setOwner(owner);
        callLog.setDomain(domain);
        callLog.setClientNumber(clientNumber);
        callLog.setTime(time);
        return callLog;
    }

    public CallLog noOneManagerPickUpThePhone(CallFromAllManagers serviceCall) {

        CallbackUser owner = serviceCall.getSite().getOwner();
        String domain = serviceCall.getSite().getDomain();
        String clientNumber = serviceCall.getClientNumber();
        Date time = new Date(serviceCall.getBeginTime());

        CallLog callLog = new CallLog();
        callLog.setOwner(owner);
        callLog.setDomain(domain);
        callLog.setClientNumber(clientNumber);
        callLog.setTime(time);
        callLog.setStatus(CallLog.Status.NO_ONE_MANAGER_PICK_UP_THE_PHONE);
        return callLog;
    }

    public CallLog createCallLog(FromBrowserCall fromBrowserCall) {

        String sid = fromBrowserCall.getSid();
        Long time = fromBrowserCall.getBeginTime();
        String clientNumber = fromBrowserCall.getClientNumber();
        CallbackUser owner = fromBrowserCall.getManager().getOwner();
        String phoneNumber = fromBrowserCall.getManager().getPhoneNumber();
        Integer duration = fromBrowserCall.getDuration();

        CallLog callLog = new CallLog();
        callLog.setSid(sid);
        callLog.setTime(new Date(time));
        callLog.setClientNumber(clientNumber);
        callLog.setManagerNumber(phoneNumber);
        callLog.setDuration(duration);
        callLog.setOwner(owner);

        if (fromBrowserCall.isStoppedDueToLackOfMoney())
            callLog.setStatus(CallLog.Status.NOT_ENOUGH_MONEY);
        else
            callLog.setStatus(CallLog.Status.CALL_FROM_BROWSER);

        return callLog;
    }

}
