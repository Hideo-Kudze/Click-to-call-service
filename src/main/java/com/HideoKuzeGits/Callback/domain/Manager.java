package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.crud.ExcludeFromCrudOperations;
import com.HideoKuzeGits.Callback.crud.Updatable;
import com.HideoKuzeGits.Callback.manager.MoreThanOneOwnerException;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 10.10.14.
 */
@Entity
public class Manager implements Serializable, Comparable<Manager> {

    @OneToOne(cascade = CascadeType.ALL)
    protected SipNumber sipNumber;
    @Id
    private String id = UsefulStaticMethods.generateUUID();
    @NotEmpty
    @Updatable
    private String name;
    @Pattern(regexp = "[\\d+]{3,}")
    @Column(unique = true)
    @Updatable
    private String phoneNumber;
    //TODO uncomment
    //@Size(min = 5)
    @ExcludeFromCrudOperations
    private String password;
    @Updatable
    private Integer priority = 1;
    private Integer calls = 0;
    @Updatable
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ServicedSite_Manager",
            joinColumns = @JoinColumn(name = "managers_id"),
            inverseJoinColumns = @JoinColumn(name = "ServicedSite_id"))
    @Updatable
    private List<ServicedSite> servicedSites;

    private boolean busy = false;
    @Updatable
    private Boolean afk = false;

    @Updatable
    private Boolean receiveCallInBrowser = false;


    public Manager() {
    }

    public Manager(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.sipNumber = null;
    }

    public SipNumber getSipNumber() {
        return sipNumber;
    }

    public void setSipNumber(SipNumber sipNumber) {
        this.sipNumber = sipNumber;
        this.phoneNumber = null;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getCalls() {
        return calls;
    }

    public void setCalls(Integer calls) {
        this.calls = calls;
    }

    public List<ServicedSite> getServicedSites() {
        return servicedSites;
    }

    public void setServicedSites(List<ServicedSite> servicedSiteDomain) {

        if (servicedSiteDomain != null && !servicedSiteDomain.isEmpty()) {
            CallbackUser callbackUser = servicedSites.get(0).getOwner();
            for (ServicedSite servicedSite : servicedSiteDomain)
                if (!servicedSite.getOwner().equals(callbackUser))
                    throw new MoreThanOneOwnerException("Manage can`t have to sites with different users");
        }
        this.servicedSites = servicedSiteDomain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isBusy() {
        return busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public Boolean getAfk() {
        return afk;
    }

    public void setAfk(Boolean afk) {
        this.afk = afk;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getReceiveCallInBrowser() {
        return receiveCallInBrowser;
    }

    public void setReceiveCallInBrowser(boolean receiveCallInBrowser) {
        this.receiveCallInBrowser = receiveCallInBrowser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(Manager m) {

        if (busy)
            return -1;

        return getCalls() - m.getCalls();
    }

    public CallbackUser getOwner() {
        return servicedSites.get(0).getOwner();
    }
}
