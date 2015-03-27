package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.wraps;

/**
 * Created by root on 04.02.15.
 */
public class MakeCallInfo {

    private String clientNumber;
    private String domain;

    public MakeCallInfo(String clientNumber, String domain) {

        this.domain = domain;
        this.clientNumber = clientNumber;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
