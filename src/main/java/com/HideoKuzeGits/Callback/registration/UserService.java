package com.HideoKuzeGits.Callback.registration;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 08.10.14.
 */

@Service
public class UserService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveUser(CallbackUser callbackUser) throws RegistrationException {

        TypedQuery<CallbackUser> query = em.createQuery("SELECT c from CallbackUser c " +
                "WHERE c.email = :email", CallbackUser.class);
        query.setParameter("email", callbackUser.getEmail());

        List<CallbackUser> resultList = query.getResultList();

        if (!resultList.isEmpty()) {
            CallbackUser userFromDatabase = resultList.get(0);

            if (userFromDatabase.isEmailConfirmed())
                throw new RegistrationException(1, "User with this email already exist.");
            else
                throw new RegistrationException(2, "Letter already sent to this email.");
        }

        em.persist(callbackUser);
    }

    @Transactional
    public void approveEmail(String approveLinkCode) throws RegistrationException {

        TypedQuery<CallbackUser> query
                = em.createQuery("select u from CallbackUser u where u.approveEmailCode = :approveLinkCode", CallbackUser.class);
        query.setParameter("approveLinkCode", approveLinkCode);
        List<CallbackUser> resultList = query.getResultList();

        if (resultList.isEmpty())
            throw new RegistrationException(3, "Link to the confirmation email is not valid.");

        CallbackUser user = resultList.get(0);
        user.setEmailConfirmed(true);
        user.setApproveEmailCode(null);
    }

    @Transactional
    public void removeRegistrationRequestsOlderThen(Date lastRegistrationDate) {
        Query query = em.createQuery("delete  from CallbackUser c where c.dateOfRegRequest < :date" +
                " AND c.emailConfirmed = false");
        query.setParameter("date", lastRegistrationDate);
        query.executeUpdate();
    }

    @Transactional
    public void removeRecoverPasswordRequestOlderThen(Date lastRegistrationDate) {
        TypedQuery<CallbackUser> query
                = em.createQuery("select c from CallbackUser c where c.recoverPassword.date < :date", CallbackUser.class);
        query.setParameter("date", lastRegistrationDate);
        List<CallbackUser> users = query.getResultList();

        for (CallbackUser user : users) {
            user.setRecoverPassword(null);
        }

    }

    public CallbackUser getUser(String email) {

        TypedQuery<CallbackUser> query
                = em.createQuery("SELECT u FROM CallbackUser u WHERE u.email = :email", CallbackUser.class);
        query.setParameter("email", email);
        List<CallbackUser> resultList = query.getResultList();

        if (resultList.isEmpty())
            return null;
        else
            return resultList.get(0);
    }

    public String getUserId(String email) {

        Query nativeQuery = em.createNativeQuery("select uuid  from CallbackUser where  email = :email");
        nativeQuery.setParameter("email", email);
        String uuid = (String) nativeQuery.getResultList().get(0);
        return uuid;
    }


    public CallbackUser getUserByRecoverPasswordToken(String recoverPasswordToken) {

        TypedQuery<CallbackUser> query
                = em.createQuery("SELECT u FROM CallbackUser u WHERE u.recoverPassword.recoverPasswordToken = :recoverPasswordToken"
                , CallbackUser.class);
        query.setParameter("recoverPasswordToken", recoverPasswordToken);
        List<CallbackUser> resultList = query.getResultList();

        if (resultList.isEmpty())
            return null;

        return resultList.get(0);
    }
}
