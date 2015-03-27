package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.crud.ExcludeFromCrudOperations;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by root on 19.10.14.
 */
@MappedSuperclass
public abstract class AbstractUserOwn {

    @ManyToOne
    @NotNull
    @ExcludeFromCrudOperations
    protected CallbackUser owner;

    public CallbackUser getOwner() {
        return owner;
    }

    public void setOwner(CallbackUser owner) {
        this.owner = owner;
    }

    public String getId() {

        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class))
                try {
                    return BeanUtils.getProperty(this, field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

}
