package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by root on 02.12.14.
 */
@Entity
public class CallLog extends AbstractUserOwn {

    @Id
    private String sid = UsefulStaticMethods.generateUUID();

    private String domain;
    private Date time;
    private String clientNumber;
    private String managerNumber;
    private Integer duration = 0;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SUCCESSFUL,
        ALL_MANAGERS_ARE_BUSY,
        NO_ONE_MANAGER_PICK_UP_THE_PHONE,
        DISCONNECTED,
        NOT_ENOUGH_MONEY,
        ANOTHER_MANGER_TOOK_CALL,
        CALL_FROM_BROWSER,
        TECHNICAL_ERROR
    }

    public CallLog() {
    }


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getManagerNumber() {
        return managerNumber;
    }

    public void setManagerNumber(String managerNumber) {
        this.managerNumber = managerNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
