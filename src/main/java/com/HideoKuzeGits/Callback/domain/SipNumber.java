package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.crud.ExcludeFromCrudOperations;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by root on 15.10.14.
 */

@Entity
@Embeddable
public class SipNumber {

    @Id
    @ExcludeFromCrudOperations
    protected String phoneNumber;
    @NotEmpty
    protected String pass;
    @NotEmpty
    @ExcludeFromCrudOperations
    protected String domain;

    public SipNumber() {
    }

    public SipNumber(String domain) {
        this.domain = domain;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String number) {
        this.phoneNumber = number;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
