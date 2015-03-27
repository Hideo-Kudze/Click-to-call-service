package com.HideoKuzeGits.Callback.domain;

import javax.persistence.Embeddable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by root on 21.02.15.
 */

@Embeddable
public class RecoverPassword {

    private String recoverPasswordToken = UUID.randomUUID().toString();
    private Date date = new Date();

    public String getRecoverPasswordToken() {
        return recoverPasswordToken;
    }

    public void setRecoverPasswordToken(String recoverPasswordToken) {
        this.recoverPasswordToken = recoverPasswordToken;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
