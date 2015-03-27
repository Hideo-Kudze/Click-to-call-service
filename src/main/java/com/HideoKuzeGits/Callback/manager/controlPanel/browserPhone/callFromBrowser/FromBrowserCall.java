package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callFromBrowser;

import com.HideoKuzeGits.Callback.callback.CalToClient;
import com.HideoKuzeGits.Callback.domain.CallLog;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.twilio.sdk.verbs.Dial;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;

/**
 * Created by root on 18.12.14.
 */


public class FromBrowserCall extends CalToClient {

    private static Logger log = Logger.getLogger(FromBrowserCall.class.getName());
    private Integer duration;
    private FromBrowserCallService fromBrowserCallService;

    public FromBrowserCall(Manager manager, EntityManager em, EntityManagerFactory emf) {
        super(manager, em, emf);
    }


    @Override
    public String getTwiML() throws TwiMLException {

        if (stopped)
            return REJECT_TWILIO_ML;

        TwiMLResponse twiMLResponse = new TwiMLResponse();
        Dial dial = new Dial(clientNumber);
        dial.setCallerId("+1 256-400-1184");
        twiMLResponse.append(dial);
        String twiMl = twiMLResponse.toEscapedXML();
        log.info("Getting twi ml for callFromBrowser to browser. Manager name " + manager.getName() + ".");
        return twiMl;
    }

    @Override
    protected void callEnds(String status) {

        log.info("Call ends with status: " + status + ". Manager name " + manager.getName() + ".");
        fromBrowserCallService.remove(sid);
        CallLog callLog = callLogFactory.createCallLog(this);
        em.persist(callLog);
    }

    public void stopDueToLackOfMoney() {

        log.info("Call ends due the luck of money. Manager name " + manager.getName() + ".");
        stopped = true;
        stoppedDueToLackOfMoney = true;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setFromBrowserCallService(FromBrowserCallService fromBrowserCallService) {
        this.fromBrowserCallService = fromBrowserCallService;
    }


}