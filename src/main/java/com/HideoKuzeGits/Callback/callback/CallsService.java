package com.HideoKuzeGits.Callback.callback;

import com.HideoKuzeGits.Callback.billing.BillingService;
import com.HideoKuzeGits.Callback.billing.CallBalanceMonitor;
import com.HideoKuzeGits.Callback.domain.CallLog;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.logs.CallLogFactory;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.BrowserPhoneApi;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callToBrowser.ToBrowserCallService;
import com.twilio.sdk.TwilioRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by root on 24.11.14.
 */
@Service
@Transactional
public class CallsService {

    private static Logger log = Logger.getLogger(CallsService.class.getName());

    private HashMap<String, CallFromAllManagers> calls = new HashMap<String, CallFromAllManagers>();


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    @Qualifier("twilioService")
    private TwilioService twilioService;

    @Autowired
    @Qualifier("toBrowserCallService")
    private ToBrowserCallService toBrowserCallService;

    @Autowired
    private CallLogFactory callLogFactory;

    @Autowired
    private BillingService billingService;

    @Autowired
    private BrowserPhoneApi onlineService;


    public void makeCall(String siteId, String clientNumber) {

        ServicedSite site = em.find(ServicedSite.class, siteId);
        site.increaseReceivedCalls();
        log.info("Call requested from site: " + site.getDomain() + " on number " + clientNumber + ".");
        List<Manager> managers = site.getManagers();

        CallLog callLog = null;
        if (isAllManagersBusy(managers))
            callLog = callLogFactory.allManagersBusy(clientNumber, site);
        if (site.getOwner().getBalance() <= 0)
            callLog = callLogFactory.notEnoughMoney(clientNumber, site);
        if (callLog != null) {
            log.info("Save callFromBrowser log with status:" + callLog.getStatus() + ".");
            em.persist(callLog);
            return;
        }


        try {
            if (site.isDistributeManagerLoad())
                callToHighestPriorityManager(clientNumber, site);
            else
                callToAllManagers(clientNumber, site);
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }

    }


    private void callToAllManagers(String clientNumber, ServicedSite site) {

        log.info("Call to all managers from site: " + site.getDomain() + " on number " + clientNumber + ".");

        List<Manager> managers = site.getManagers();

        List<CallFromAllManagers> allCallsToOneClient = new ArrayList<CallFromAllManagers>();

        for (Manager manager : managers) {
            CallFromAllManagers callFromAllManagers = null;


            try {

                String phoneNumber = manager.getPhoneNumber();
                if (manager.getReceiveCallInBrowser() && onlineService.isManagerOnline(phoneNumber))
                    callFromAllManagers = makeCallToBrowser(clientNumber, site, manager);
                else
                    callFromAllManagers = makeCall(clientNumber, site, manager);

            } catch (TwilioRestException e) {
                manager.setBusy(false);
                em.merge(manager);
                CallLog callLog = callLogFactory.technicalError(clientNumber, site);
                em.persist(callLog);
                e.printStackTrace();
            }
            if (callFromAllManagers != null) {
                log.info("Manager " + manager.getName() + " on number " + clientNumber + ".");
                allCallsToOneClient.add(callFromAllManagers);
                callFromAllManagers.setAllCallsToOneClient(allCallsToOneClient);
            }

        }
    }


    private boolean isAllManagersBusy(List<Manager> managers) {

        for (Manager manager : managers) {
            if (!manager.isBusy() || !manager.getAfk())
                return false;
        }

        return true;
    }


    private void callToHighestPriorityManager(String clientNumber, ServicedSite site) throws TwilioRestException {

        //List sorted in natural order.
        Manager manager = site.getManagers().get(0);
        log.info("Call to highest priority manager : " + manager.getName()
                + " from site: " + site.getDomain()
                + " on number " + clientNumber + ".");
        makeCall(clientNumber, site, manager);
    }

    public CallFromAllManagers makeCall(String clientNumber, ServicedSite site, Manager manager) throws TwilioRestException {
        return makeCall(clientNumber, site, manager, twilioService);
    }

    public CallFromAllManagers makeCallToBrowser(String clientNumber, ServicedSite site, Manager manager) throws TwilioRestException {
        return makeCall(clientNumber, site, manager, toBrowserCallService);
    }

    private CallFromAllManagers makeCall(String clientNumber, ServicedSite site, Manager manager, TwilioService twilioService) throws TwilioRestException {


        log.info("Make call from site: " + site.getDomain()
                + " on number " + clientNumber
                + " manager: " + manager.getName() + ".");

        if (manager.isBusy() || manager.getAfk()) {
            log.info("Manager " + manager.getName() + "is busy.");
            return null;
        }


        String managerPhone = manager.getPhoneNumber();
        String sid = twilioService.makeCall(managerPhone, site.getDomain());

        CallFromAllManagers callFromAllManagers = new CallFromAllManagers(manager, em, emf);
        callFromAllManagers.setSite(site);
        callFromAllManagers.setClientNumber(clientNumber);
        callFromAllManagers.setSid(sid);
        callFromAllManagers.setCallsService(this);
        callFromAllManagers.setTwilio(twilioService);
        callFromAllManagers.setCallLogFactory(callLogFactory);

        add(callFromAllManagers);


        log.info("Manager " + manager.getName() + " calls to " + clientNumber + ".");
        return callFromAllManagers;
    }

    private ServicedSite getServicedSite(String domain) {

        TypedQuery<ServicedSite> query
                = em.createQuery("SELECT c FROM ServicedSite c WHERE c.domain = :domain", ServicedSite.class);
        query.setParameter("domain", domain);

        return query.getSingleResult();
    }


    public void add(CallFromAllManagers call) {
        calls.put(call.getSid(), call);
    }

    public CallFromAllManagers remove(Object key) {
        return calls.remove(key);
    }

    public String getTwiML(String callSid) {
        CallFromAllManagers callFromAllManagers = calls.get(callSid);
        return callFromAllManagers.getTwiML();
    }


    public void callEnds(String callSid, String status) {

        CallFromAllManagers callFromAllManagers = calls.get(callSid);
        if (callFromAllManagers != null)
            callFromAllManagers.callEndsAsync(status);
    }

    public void managerPickUpPhone(String callSid) {

        CallFromAllManagers callFromAllManagers = calls.get(callSid);

        CallBalanceMonitor callBalanceMonitor = new CallBalanceMonitor(callFromAllManagers, billingService);
        new Thread(callBalanceMonitor).start();
        callFromAllManagers.managerPickUpPhone();
    }

    public void stopCall(String sid) {

        CallFromAllManagers callFromAllManagers = calls.get(sid);
        if (callFromAllManagers != null)
            callFromAllManagers.stopCall();
    }

    public CallFromAllManagers get(String callSid) {
        return calls.get(callSid);
    }
}
