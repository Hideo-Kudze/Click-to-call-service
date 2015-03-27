package com.HideoKuzeGits.Callback.consultant.domain;

import com.HideoKuzeGits.Callback.crud.Updatable;

import javax.persistence.Entity;

/**
 * Created by root on 09.03.15.
 */

@Entity
public class UserInformation extends Addressee {

    @Updatable
    private String domain;
    @Updatable
    private String phone;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
