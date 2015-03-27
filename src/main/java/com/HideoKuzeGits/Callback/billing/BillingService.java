package com.HideoKuzeGits.Callback.billing;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * Created by root on 04.12.14.
 */
@Service
public class BillingService {


    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Integer increaseBalance(CallbackUser user, Integer seconds) {

        user = em.find(CallbackUser.class, user.getUuid());

        Integer balance = user.getBalance();
        balance = balance - seconds;
        user.setBalance(balance);
        em.merge(user);
        return balance;
    }

}
