package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.crud.ExcludeFromCrudOperations;
import com.HideoKuzeGits.Callback.crud.Updatable;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by root on 08.10.14.
 */

@Entity
public class CallbackUser {


    //TODO uncomment
    //@Size(min = 5)
    @ExcludeFromCrudOperations
    private String password;
    //TODO uncomment
    //@Email
    @NotEmpty
    @Column(unique = true)
    @ExcludeFromCrudOperations
    private String email;
    @NotEmpty
    @Updatable
    private String name;
    @ExcludeFromCrudOperations
    private Date dateOfRegRequest = new Date();
    @Id
    @ExcludeFromCrudOperations
    private String uuid = UsefulStaticMethods.generateUUID();

    @ExcludeFromCrudOperations
    private Boolean emailConfirmed = false;
    @ExcludeFromCrudOperations
    private Boolean allowed = true;
    @ExcludeFromCrudOperations
    private Integer balance = 0;
    @ExcludeFromCrudOperations
    private String approveEmailCode = UsefulStaticMethods.generateUUID();
    @ExcludeFromCrudOperations
    @Embedded
    private RecoverPassword recoverPassword;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean confirmEmail) {
        this.emailConfirmed = confirmEmail;
    }

    public Boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Date getDateOfRegRequest() {
        return dateOfRegRequest;
    }

    public void setDateOfRegRequest(Date dateOfRegRequest) {
        this.dateOfRegRequest = dateOfRegRequest;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public void increaseBalance(int amount) {
        balance = balance + amount;
    }

    public void reduceBalance(int amount) {
        balance = balance - amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApproveEmailCode() {
        return approveEmailCode;
    }

    public void setApproveEmailCode(String approveLinkCode) {
        this.approveEmailCode = approveLinkCode;
    }

    public RecoverPassword getRecoverPassword() {
        return recoverPassword;
    }

    public void setRecoverPassword(RecoverPassword recoverPasswordCode) {
        this.recoverPassword = recoverPasswordCode;
    }
}
