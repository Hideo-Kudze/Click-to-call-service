package com.HideoKuzeGits.Callback.callback;


import com.HideoKuzeGits.Callback.domain.CallLog;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.domain.WidgetProperties;
import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.BrowserPhoneApi;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.verbs.Dial;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

//Transactional from CallsService
public class CallFromAllManagers extends CalToClient {

    private static Logger log = Logger.getLogger(CallFromAllManagers.class.getName());

    protected ServicedSite site;
    private List<CallFromAllManagers> allCallsToOneClient = new ArrayList<CallFromAllManagers>();

    private CallsService callsService;

    protected BrowserPhoneApi onlineService;

    public CallFromAllManagers(Manager manager, EntityManager em, EntityManagerFactory emf) {

        super(manager, em, emf);
        this.manager = manager;

        log.info("Created new CallToClient with sid: " + getSid() + ".");
    }


    @Override
    public String getTwiML() {

        if (isStopped()) {
            log.info("Return twilioMl: " + REJECT_TWILIO_ML + ".");
            return REJECT_TWILIO_ML;
        }

        String message = getMessage();
        try {
            String twiML = getTwiML(message);
            log.info("Return twilioMl: " + twiML + ".");
            return twiML;
        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getTwiML(String message) throws TwiMLException {

        TwiMLResponse twiMLResponse = new TwiMLResponse();

        //TODO uncomment
       /* Say say = new Say(message);
        say.setLanguage("ru-RU");
        say.setVoice("women");
        twiMLResponse.append(say);
*/

        Dial dial = new Dial(getClientNumber());
        dial.setCallerId("+1 256-400-1184");
        twiMLResponse.append(dial);

        return twiMLResponse.toEscapedXML();
    }


    private String getMessage() {

        InputStream in = CallFromAllManagers.class.getResourceAsStream("/DefaultAlertForManager.txt");
        String message = new Scanner(in).useDelimiter("\\A").next();
        String siteName = site.getSiteName();
        boolean containsLatinCharacters = siteName.matches(".*[a-zA-Z].*");

        if (containsLatinCharacters)
            siteName = "</Say><Say voice=\"women\" language=\"en-EN\">"
                    + siteName
                    + "</Say><Say voice=\"women\" language=\"ru-RU\">";

        message = message.replace("[site_name]", siteName);
        log.info("Return message: " + message + ".");
        return message;
    }


    private String getSmsMessage() {

        Configuration cfg = new Configuration();
        try {
            cfg.setDirectoryForTemplateLoading(new File("/"));
            URL path = getClass().getResource("/sms.ftl");
            Template template = cfg.getTemplate(path.getFile());
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("dateTime", new Date(getBeginTime()));
            data.put("siteName", site.getSiteName());
            data.put("clientNumber", getClientNumber());
            StringWriter stringWriter = new StringWriter();
            template.process(data, stringWriter);
            return stringWriter.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void managerPickUpPhone() {

        log.info("Manager " + manager.getName() + " pick up the phone.");

        if (site.isDistributeManagerLoad())
            return;

        if (!isStopped())
            synchronized (allCallsToOneClient) {
                stopCalls();
            }

        String phoneNumber = manager.getPhoneNumber();
        WidgetProperties widgetProperties = site.getWidgetProperties();
        if (!isStopped() && widgetProperties.isSendSms() && !onlineService.isManagerOnline(phoneNumber))
            sandSmsToManager();

    }

    private void stopCalls() {


        for (CallFromAllManagers callFromAllManagers : allCallsToOneClient) {
            String anotherCallSid = callFromAllManagers.getSid();

            if (!anotherCallSid.equals(sid) && !isStopped()) {
                callFromAllManagers.setStopped(true);
                callsService.stopCall(anotherCallSid);
            }

        }
    }

    private void sandSmsToManager() {

        String smsBody = getSmsMessage();
        try {
            getTwilio().sendSMS(manager.getPhoneNumber(), smsBody);
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void callEnds(String status) {

        calEnds = true;
        this.status = status;
        callsService.remove(sid);
        Integer calls = manager.getCalls();
        calls++;
        manager.setCalls(calls);

        log.info("Call ends with status :" + status + ".");

        CallLog callLog = callLogFactory.createCallLog(this);
        if (callLog != null) {
            em.persist(callLog);
            log.info("Save callFromBrowser log with status:" + callLog.getStatus() + ".");
        }


        //<editor-fold desc="Последовательный вызов">
       /* if ("completed".equals(status) && site.isDistributeManagerLoad()) {
            List<Manager> managers = site.getManagers();
            int managerIndex = managers.indexOf(manager);
            managerIndex++;
            if (managerIndex >= managers.size())
                return;

            Manager nextManager = managers.get(managerIndex);

            try {
                callsService.makeCall(clientNumber, site, manager);
            } catch (TwilioRestException e) {
                e.printStackTrace();
            }
        }*/
        //</editor-fold>

    }

    public boolean isAllCallsFromGroupEnds() {

        for (CallFromAllManagers callFromAllManagers : allCallsToOneClient)
            if (!callFromAllManagers.isCalEnds())
                return false;

        return true;
    }

    /////////////////////////////////////////////Getters and Setters///////////////////////////////////////////////


    public void setAllCallsToOneClient(List<CallFromAllManagers> allCallsToOneClient) {
        this.allCallsToOneClient = allCallsToOneClient;
    }

    public void setCallsService(CallsService callsService) {
        this.callsService = callsService;
    }

    public void setOnlineService(BrowserPhoneApi onlineService) {
        this.onlineService = onlineService;
    }

    public ServicedSite getSite() {
        return site;
    }

    public void setSite(ServicedSite site) {
        this.site = site;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (!(obj instanceof CallFromAllManagers))
            return false;

        CallFromAllManagers callToCompere = (CallFromAllManagers) obj;
        return getSid().equals(callToCompere.getSid());
    }

    @Override
    public int hashCode() {
        return sid.hashCode();
    }
}
