package com.HideoKuzeGits.Callback.consultant.domain;

import com.HideoKuzeGits.Callback.crud.ExcludeFromCrudOperations;
import com.HideoKuzeGits.Callback.crud.Updatable;
import com.HideoKuzeGits.Callback.domain.ServicedSite;

import javax.persistence.*;
import java.util.List;

/**
 * Created by root on 09.03.15.
 */

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ConsultantManager extends Addressee {

    @ExcludeFromCrudOperations
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ServicedSite_СonsultantManagers",
            joinColumns = @JoinColumn(name = "consultantManagers_id"),
            inverseJoinColumns = @JoinColumn(name = "ServicedSite_id"))
    @Updatable
    private List<ServicedSite> servicedSites;

    public List<ServicedSite> getServicedSites() {
        return servicedSites;
    }

    public void setServicedSites(List<ServicedSite> servicedSites) {
        this.servicedSites = servicedSites;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
