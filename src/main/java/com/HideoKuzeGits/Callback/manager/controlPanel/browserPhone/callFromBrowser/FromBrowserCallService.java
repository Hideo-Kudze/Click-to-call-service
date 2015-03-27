package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callFromBrowser;

import com.HideoKuzeGits.Callback.billing.BillingService;
import com.HideoKuzeGits.Callback.billing.CallBalanceMonitor;
import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.logs.CallLogFactory;
import com.HideoKuzeGits.Callback.manager.ManagerDAO;
import com.twilio.sdk.verbs.TwiMLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by root on 18.12.14.
 */

@Service
@Transactional
public class FromBrowserCallService {

    Map<String, FromBrowserCall> callsMap = new HashMap<String, FromBrowserCall>();
    @PersistenceContext
    private EntityManager em;
    @Autowired
    @Qualifier("twilioService")
    private TwilioService twilio;
    @Autowired
    private BillingService billingService;
    @Autowired
    private CallLogFactory callLogFactory;
    @Autowired
    private ManagerDAO managerDAO;
    @Autowired
    private EntityManagerFactory emf;


    private static Logger log = Logger.getLogger(FromBrowserCallService.class.getName());

    public void callBegins(String callSid, String clientNumber, String managerPhone) {


        log.info("Call " + callSid + " begins.");
        Manager manager = managerDAO.getManagerByPhone(managerPhone);

        if (manager.getServicedSites().isEmpty()) {
            log.info("From browser call stopped because manager is not a member of any one project.");
            return;
        }

        FromBrowserCall fromBrowserCall = createCallFromBrowser(callSid, clientNumber, manager);

        if (manager.getOwner().getBalance() <= 0)
            fromBrowserCall.stopDueToLackOfMoney();


        callsMap.put(callSid, fromBrowserCall);
        CallBalanceMonitor callBalanceMonitor = new CallBalanceMonitor(fromBrowserCall, billingService);
        new Thread(callBalanceMonitor).start();
    }

    private FromBrowserCall createCallFromBrowser(String callSid, String clientNumber, Manager manager) {
        FromBrowserCall fromBrowserCall = new FromBrowserCall(manager, em, emf);
        fromBrowserCall.setSid(callSid);
        fromBrowserCall.setClientNumber(clientNumber);
        fromBrowserCall.setTwilio(twilio);
        fromBrowserCall.setFromBrowserCallService(this);
        fromBrowserCall.setCallLogFactory(callLogFactory);
        return fromBrowserCall;
    }

    public String getTwiMl(String callSid) {

        log.info("Getting twi ml for callFromBrowser " + callSid + ".");
        FromBrowserCall fromBrowserCall = callsMap.get(callSid);
        try {
            return fromBrowserCall.getTwiML();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void callEnds(String callSid, String duration) {

        log.info("Call " + callSid + " ends.");
        FromBrowserCall fromBrowserCall = callsMap.get(callSid);
        if (fromBrowserCall == null) return;
        fromBrowserCall.setDuration(Integer.valueOf(duration));
        fromBrowserCall.callEndsAsync("");

        String phoneNumber = fromBrowserCall.getManager().getPhoneNumber();

    }

    public FromBrowserCall remove(Object key) {
        return callsMap.remove(key);
    }


}
