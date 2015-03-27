package com.HideoKuzeGits.Callback.servicedSite;

import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.registration.ActiveUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by root on 14.01.15.
 */
@Controller
public class ServicedSiteController {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "servicedSites", method = RequestMethod.GET)
    public String servicedSitesPage(@ActiveUser User user, ModelMap modelMap) {

        String email = user.getUsername();
        TypedQuery<ServicedSite> query
                = em.createQuery("SELECT s FROM ServicedSite s WHERE s.owner.email = :email", ServicedSite.class);
        query.setParameter("email", email);
        List<ServicedSite> servicedSites = query.getResultList();
        modelMap.put("servicedSites", servicedSites);
        return "servicedSites";
    }
}
