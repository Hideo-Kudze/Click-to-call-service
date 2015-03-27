package com.HideoKuzeGits.Callback.billing;

import com.HideoKuzeGits.Callback.callback.CalToClient;
import com.HideoKuzeGits.Callback.callback.TwilioService;
import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.Call;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by root on 04.12.14.
 */
public class CallBalanceMonitor implements Runnable {

    private static Logger log = Logger.getLogger(CallBalanceMonitor.class.getName());

    private CalToClient calToClient;
    private BillingService billingService;


    public CallBalanceMonitor(CalToClient calToClient, BillingService billingService) {
        this.calToClient = calToClient;
        this.billingService = billingService;
        log.info("Created callFromBrowser monitor for callToClient with sid:" + calToClient.getSid() + ".");
    }


    @Override
    public void run() {

        TwilioService twilio = calToClient.getTwilio();
        CallbackUser owner = calToClient.getManager().getOwner();
        String sid = calToClient.getSid();

        Call callTomManager = twilio.getCallToManager(sid);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.ENGLISH);
        Date date = null;
        try {
            date = simpleDateFormat.parse(callTomManager.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long beginTime = date.getTime();
        long previousDurationValue = 0;
        long serviceDurationValue = 0;

        log.info("Start monitoring callFromBrowser with sid: " + sid + ".");

        while (callTomManager.getStatus().equals("in-progress") ||
                callTomManager.getStatus().equals("ringing")) {
            sleepSeconds(1);
            long currentTime = getCurrentTime();
            serviceDurationValue = currentTime - beginTime;
            int durationDiff = (int) (serviceDurationValue - previousDurationValue) / 1000;
            Integer balance = billingService.increaseBalance(owner, durationDiff);
            if (balance <= 0)
                stopCall(twilio, sid);
            previousDurationValue = serviceDurationValue;
            callTomManager = twilio.getCallToManager(sid);
        }

        String twilioDurationString = callTomManager.getDuration();
        Integer twilioDurationValue = Integer.valueOf(twilioDurationString);
        int increaseDuration = (int) (twilioDurationValue - (serviceDurationValue + 1) / 1000);
        billingService.increaseBalance(owner, increaseDuration);
    }

    private long getCurrentTime() {
        String TIME_SERVER = "time-a.nist.gov";
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = null;
        TimeInfo timeInfo = null;
        try {
            inetAddress = InetAddress.getByName(TIME_SERVER);
            timeInfo = null;
            timeInfo = timeClient.getTime(inetAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeInfo.getMessage().getTransmitTimeStamp().getTime();
    }

    private void stopCall(TwilioService twilio, String sid) {
        try {
            twilio.stopCall(sid);
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
        calToClient.setStoppedDueToLackOfMoney(true);
        log.info("Call with sid:" + calToClient.getSid() + " was stopped due the lack of money.");
    }

    private void sleepSeconds(int i) {
        try {
            Thread.sleep(1000 * i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
