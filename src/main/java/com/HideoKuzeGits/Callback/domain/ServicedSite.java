package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.consultant.domain.ConsultantManager;
import com.HideoKuzeGits.Callback.crud.Updatable;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 16.10.14.
 */

@Entity
public class ServicedSite extends AbstractUserOwn {

    @Id
    private String id = UsefulStaticMethods.generateUUID();
    @Column(unique = true)
    private String domain;
    @Updatable
    private Boolean distributeManagerLoad = false;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ServicedSite_Manager",
            joinColumns = @JoinColumn(name = "ServicedSite_id"),
            inverseJoinColumns = @JoinColumn(name = "managers_id"))
    @Updatable
    private List<Manager> managers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ServicedSite_СonsultantManagers",
            joinColumns = @JoinColumn(name = "ServicedSite_id"),
            inverseJoinColumns = @JoinColumn(name = "consultantManagers_id"))
    @Updatable
    private List<ConsultantManager> consultantManagers;

    private String siteName;

    @Updatable
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private WidgetProperties widgetProperties;

    private Integer callsReceived = 0;

    public ServicedSite() {

        if (widgetProperties == null) {
            widgetProperties = new WidgetProperties();
            widgetProperties.setId(id);
        }

    }

    public ServicedSite(String domain, List<Manager> managers, String siteName) {
        this();
        this.domain = domain;
        this.managers = managers;
        this.siteName = siteName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean isDistributeManagerLoad() {
        return distributeManagerLoad;
    }

    public Boolean getDistributeManagerLoad() {
        return distributeManagerLoad;
    }

    public void setDistributeManagerLoad(Boolean distributeManagerLoad) {
        this.distributeManagerLoad = distributeManagerLoad;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        Collections.sort(managers);
        this.managers = managers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        widgetProperties.setId(id);
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String name) {
        this.siteName = name;
    }

    public Integer getCallsReceived() {
        return callsReceived;
    }

    public void setCallsReceived(Integer callsReceived) {
        this.callsReceived = callsReceived;
    }

    public void increaseReceivedCalls() {
        callsReceived++;
    }

    public WidgetProperties getWidgetProperties() {
        return widgetProperties;
    }

    public void setWidgetProperties(WidgetProperties widgetProperties) {
        this.widgetProperties = widgetProperties;
    }

    public List<ConsultantManager> getConsultantManagers() {
        return consultantManagers;
    }

    public void setConsultantManagers(List<ConsultantManager> consultantManagers) {
        this.consultantManagers = consultantManagers;
    }

    @Override
    public void setOwner(CallbackUser owner) {

        widgetProperties.setOwner(owner);
        super.setOwner(owner);
    }
}
