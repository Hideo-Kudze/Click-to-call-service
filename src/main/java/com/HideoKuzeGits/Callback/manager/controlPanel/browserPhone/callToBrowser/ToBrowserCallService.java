package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.callToBrowser;

import com.HideoKuzeGits.Callback.callback.CallFromAllManagers;
import com.HideoKuzeGits.Callback.callback.CallsService;
import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.manager.ManagerDAO;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.BrowserPhoneApi;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by root on 15.01.15.
 */

//@Service
@Controller
public class ToBrowserCallService extends TwilioService {

    private static Logger log = Logger.getLogger(ToBrowserCallService.class.getName());


    @Autowired
    private BrowserPhoneApi browserPhoneApi;

    @Autowired
    private CallsService callsService;

    @Autowired
    private ManagerDAO managerDAO;

    private BiMap<String, String> callUuidManagerNumberMap = HashBiMap.create();
    private Map<String, Thread> callUuidMonitorMap = new HashMap<String, Thread>();

    @Autowired
    private BrowserPhoneApi browserPhoneOnlineService;

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/twilio/call")
    public String makeCall(final String managerNumber, String domain) throws TwilioRestException {

        Thread managerInBrowserPhoneMonitor = new Thread() {

            @Override
            public void run() {


                for (int i = 0; i < 10; i++) {

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        log.info("Monitor for call from manager with number " + managerNumber + "was interrupted.");
                        return;
                    }

                    if (!browserPhoneOnlineService.isManagerOnline(managerNumber)) {
                        break;
                    }
                }

                String callSid = callUuidManagerNumberMap.inverse().get(managerNumber);
                if (callSid == null) return;
                Call call = getCallToManager(callSid);
                try {
                    call.getStatus();
                    log.info("Monitor stops. Call was successfully created.");
                    return;
                } catch (Exception e) {
                }

                log.info("Monitor remove call because call was not created.");

                remove(callSid);
                callsService.callEnds(callSid, "no-answer");
            }
        };
        managerInBrowserPhoneMonitor.start();
        String uuid = UUID.randomUUID().toString();
        callUuidMonitorMap.put(uuid, managerInBrowserPhoneMonitor);


        browserPhoneApi.makeCall(managerNumber, domain);
        callUuidManagerNumberMap.put(uuid, managerNumber);

        log.info("Male call to browser. Manager number: " + managerNumber + ".");
        return uuid;
    }


    @Override
    public void stopCall(String callSid) throws TwilioRestException {

        log.info("Stop call " + callSid + " in browser.");
        String managerNumber = callUuidManagerNumberMap.get(callSid);
        browserPhoneApi.stopCall(managerNumber);
    }


    public void managerPickupThePhone(String callSid, String managerNumber) {

        log.info("Manager pickup the phone. Call sid: " + callSid + ".");

        String uuid = callUuidManagerNumberMap.inverse().get(managerNumber);
        changeCallId(uuid, callSid);

        Thread monitor = callUuidMonitorMap.get(callSid);
        if (monitor == null) return;
        monitor.interrupt();
        callUuidMonitorMap.remove(callSid);
    }

    //TODO убрать баг с синхронизацией. Если ид меняется во время завершения звнков в stopCall приходит старый id.
    public void changeCallId(String uuid, String callSid) {

        log.info("Change call id from" + uuid + " to " + callSid + " .");

        if (uuid.equals(callSid)) return;

        String managerNumber = callUuidManagerNumberMap.remove(uuid);
        callUuidManagerNumberMap.put(callSid, managerNumber);


        Thread monitor = callUuidMonitorMap.remove(uuid);
        callUuidMonitorMap.put(callSid, monitor);


        CallFromAllManagers callFromAllManagers = callsService.get(uuid);
        callFromAllManagers.setSid(callSid);
        callsService.add(callFromAllManagers);
        callsService.remove(uuid);
    }


    public String getUuid(String managerNumber) {

        return callUuidManagerNumberMap.inverse().get(managerNumber);
    }


    public String remove(String callSid) {

        log.info("Remove call " + callSid + " .");

        callUuidMonitorMap.remove(callSid);
        return callUuidManagerNumberMap.remove(callSid);
    }

    public boolean containsCall(String managerNumber) {

        return callUuidManagerNumberMap.containsValue(managerNumber);
    }
}
