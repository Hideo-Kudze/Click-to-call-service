package com.HideoKuzeGits.Callback.callback;

import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.logs.CallLogFactory;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.verbs.TwiMLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Created by root on 18.12.14.
 */
public abstract class CalToClient {

    protected static final String REJECT_TWILIO_ML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Response>\n" +
            "    <Reject />\n" +
            "</Response>";

    protected EntityManager em;
    protected EntityManagerFactory emf;
    protected String sid;
    protected String clientNumber;
    protected Manager manager;
    protected TwilioService twilio;
    protected CallLogFactory callLogFactory;
    protected Long beginTime;
    protected String status;
    protected boolean stopped = false;
    protected boolean stoppedDueToLackOfMoney = false;
    protected boolean calEnds = false;


    public CalToClient(Manager manager, EntityManager em, EntityManagerFactory emf) {

        manager.setBusy(true);
        em.merge(manager);
        em.flush();

        this.manager = manager;
        this.em = em;
        this.emf = emf;
        this.beginTime = System.currentTimeMillis();
    }

    public abstract String getTwiML() throws TwiMLException;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setCallLogFactory(CallLogFactory callLogFactory) {
        this.callLogFactory = callLogFactory;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public boolean isStoppedDueToLackOfMoney() {
        return stoppedDueToLackOfMoney;
    }

    public void setStoppedDueToLackOfMoney(boolean stoppedDueToLackOfMoney) {
        this.stoppedDueToLackOfMoney = stoppedDueToLackOfMoney;
    }

    public TwilioService getTwilio() {
        return twilio;
    }

    ;

    public void setTwilio(TwilioService twilio) {
        this.twilio = twilio;
    }

    public void callEndsAsync(final String status) {

        new Thread() {

            @Override
            public void run() {
                em = emf.createEntityManager();
                em.getTransaction().begin();
                manager.setBusy(false);
                em.merge(manager);
                em.flush();

                callEnds(status);

                em.getTransaction().commit();
            }

        }.start();
    }

    protected abstract void callEnds(String status);

    public void stopCall() {

        stopped = true;
        calEnds = true;
        try {
            getTwilio().stopCall(sid);
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
    }

    public boolean isCalEnds() {
        return calEnds;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public Manager getManager() {
        return manager;
    }

}
