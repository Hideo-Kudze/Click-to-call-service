package com.HideoKuzeGits.Callback.consultant.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.crud.Updatable;
import com.HideoKuzeGits.Callback.domain.AbstractUserOwn;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by root on 09.03.15.
 */
@Entity
public abstract class Addressee extends AbstractUserOwn {

    @Id
    private String id = UsefulStaticMethods.generateUUID();
    @NotEmpty
    @Updatable
    private String name;
    @Updatable
    @Email
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
