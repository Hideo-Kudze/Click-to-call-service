package com.HideoKuzeGits.Callback.servicedSite;

import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.registration.ActiveUser;
import com.google.gson.Gson;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by root on 15.10.14.
 */

@Controller
@Transactional
@RequestMapping(value = "servicedSite", method = RequestMethod.POST)
public class ServicedSiteCRUDAjaxController {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String create(@ActiveUser User activeUser, @RequestBody ServicedSite servicedSite) {

        try {
            String email = activeUser.getUsername();
            TypedQuery<CallbackUser> query
                    = em.createQuery("SELECT u FROM CallbackUser u WHERE u.email = :email", CallbackUser.class);
            query.setParameter("email", email);

            CallbackUser callbackUser = query.getSingleResult();
            servicedSite.setOwner(callbackUser);
            em.persist(servicedSite);
            return "{\"status\" :  1}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\" :  1, message : \"" + e.getMessage() + "\"}";
        }

    }


    @RequestMapping(value = "read", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String read(@ActiveUser User activeUser) {

        try {
            String email = activeUser.getUsername();
            TypedQuery<ServicedSite> query = em.createQuery("SELECT DISTINCT m from ServicedSite m " +
                    "INNER JOIN m.owner c WHERE c.email = :email", ServicedSite.class);
            query.setParameter("email", email);


            List<ServicedSite> ServicedSiteList = query.getResultList();
            String servicedSiteListJson = new Gson().toJson(ServicedSiteList);
            return servicedSiteListJson;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\" :  1, message : \"" + e.getMessage() + "\"}";
        }

    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String update(@ActiveUser User activeUser, @RequestBody ServicedSite servicedSite) {
        try {

            String email = activeUser.getUsername();
            ServicedSite servicedSiteFromDatabase = em.find(ServicedSite.class, servicedSite.getDomain());

            if (!servicedSiteFromDatabase.getOwner().getEmail().equals(email))
                return "{\"status\" :  1, \"message\" : 'Not enough permissions.'}";

            Boolean distributeManagerLoad = servicedSite.isDistributeManagerLoad();

            if (servicedSiteFromDatabase == null)
                return "{\"status\" :  0,  \"message\" : 'servicedSite don`t exist'.}";

            if (distributeManagerLoad != null)
                servicedSiteFromDatabase.setDistributeManagerLoad(distributeManagerLoad);

            return "{\"status\" :  1}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\" :  1, message : \"" + e.getMessage() + "\"}";
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String delete(@ActiveUser User activeUser, @RequestBody String domain) {

        try {

            String email = activeUser.getUsername();
            domain = domain.substring(1, domain.length() - 1);
            ServicedSite servicedSiteFromDatabase = em.find(ServicedSite.class, domain);

            if (servicedSiteFromDatabase == null)
                return "{\"status\" :  0,  \"message\" : 'servicedSite don`t exist'.}";

            if (!servicedSiteFromDatabase.getOwner().getEmail().equals(email))
                return "{\"status\" :  1, \"message\" : 'Not enough permissions.'}";


            em.remove(servicedSiteFromDatabase);
            return "{\"status\" :  1}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\" :  1, message : \"" + e.getMessage() + "\"}";
        }

    }


}
