package com.HideoKuzeGits.Callback.manager;

import com.HideoKuzeGits.Callback.domain.Manager;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.logging.Logger;

/**
 * Created by root on 25.12.14.
 */
@Service
public class ManagerDAO {

    private static Logger log = Logger.getLogger(ManagerDAO.class.getName());


    @PersistenceContext
    private EntityManager em;

    public Manager getManagerByPhone(String managerPhone) {

        log.info("Getting manager from database with phone " + managerPhone + ".");
        TypedQuery<Manager> query
                = em.createQuery("SELECT m FROM Manager m WHERE m.phoneNumber = :phone", Manager.class);
        query.setParameter("phone", managerPhone);
        return query.getSingleResult();

    }
}
