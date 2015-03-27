package com.HideoKuzeGits.Callback.callback;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.client.TwilioCapability;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.CallList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by root on 30.10.14.
 */
@Service
public class TwilioService {

    public static final String DEFAULT_FROM_NUMBER = "+12564001184";
    public static final String ACCOUNT_SID = "ACb902ae96e5a611aa1b57bd851864be09";
    public static final String AUTH_TOKEN = "b928fab7fba76493b3f3e15e76e37a0a";
    //Test conditionals
    /*     public static final String ACCOUNT_SID = "AC5732cb584a0b43136f04ba6c0d737588";
        public static final String AUTH_TOKEN = "cd6bd591814502d0546080645d0e06e9";*/
    private static final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    private static final String APP_SID = "AP1bb23c8d5b5061e0fa1f4a8f389b3333";
    private static Logger log = Logger.getLogger(TwilioService.class.getName());


    public Call getCallToManager(String sid) {

        return client.getAccount().getCall(sid);
    }

    public Call getCallToClient(String sid) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ParentCallSid", sid);
        CallList calls = client.getAccount().getCalls(params);
        if (!calls.iterator().hasNext())
            return null;
        return calls.getPageData().get(0);
    }

    public String makeCall(String managerNumber, String domain) throws TwilioRestException {

        log.info("Making callFromBrowser to manager " + managerNumber + ".");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", managerNumber));
        params.add(new BasicNameValuePair("From", DEFAULT_FROM_NUMBER));
        params.add(new BasicNameValuePair("Url", "http://77.47.228.33:8080/twilio/url"));
        params.add(new BasicNameValuePair("StatusCallback", "http://77.47.228.33:8080/twilio/statusCallback"));
        params.add(new BasicNameValuePair("FallbackUrl", "http://77.47.228.33:8080/twilio/fallback"));
        CallFactory callFactory = client.getAccount().getCallFactory();
        Call call = callFactory.create(params);
        return call.getSid();
    }

    public void stopCall(String callSid) throws TwilioRestException {
        log.info("Stop callFromBrowser to " + callSid + ".");
        Call call = client.getAccount().getCall(callSid);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Method", "GET"));
        params.add(new BasicNameValuePair("Status", "completed"));
        call.update(params);
    }

    public Message sendSMS(String to, String message) throws TwilioRestException {
        log.info("SendSMS  to " + to + ". \n Message body: " + message + ".");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Body", message));
        params.add(new BasicNameValuePair("To", to));
        params.add(new BasicNameValuePair("From", DEFAULT_FROM_NUMBER));
        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        return messageFactory.create(params);
    }

    public String generateToken(String alias) throws TwilioCapability.DomainException {

        TwilioCapability twilioCapability = new TwilioCapability(ACCOUNT_SID, AUTH_TOKEN);
        twilioCapability.allowClientOutgoing(APP_SID);
        twilioCapability.allowClientIncoming(alias);
        return twilioCapability.generateToken();
    }

}
