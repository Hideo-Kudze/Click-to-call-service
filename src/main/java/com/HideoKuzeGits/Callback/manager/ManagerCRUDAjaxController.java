package com.HideoKuzeGits.Callback.manager;

import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.registration.ActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by root on 15.10.14.
 */

@Controller
@Transactional
@RequestMapping(value = "/serviceManager", method = RequestMethod.POST)
public class ManagerCRUDAjaxController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ManagerDAO managerDAO;

    @RequestMapping(value = "create")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String create(@ActiveUser User activeUser, @RequestBody Manager manager) {

        Collection<GrantedAuthority> authorities = activeUser.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String authorityString = authority.getAuthority();
            if (authorityString.equals("ROLE_USER"))
                break;
            return "{\"status\" :  1, \"message\" : 'Not enough permissions.'}";
        }


        List<ServicedSite> servicedSites = manager.getServicedSites();
        ArrayList<ServicedSite> servicedSitesFromDatabase = new ArrayList<ServicedSite>();

        if (servicedSites != null && !servicedSites.isEmpty())
            for (ServicedSite site : servicedSites) {
                String domain = site.getDomain();
                ServicedSite siteFromDatabase = em.find(ServicedSite.class, domain);
                if (siteFromDatabase == null)
                    return "{\"status\" :  1, massage: \"There is no serviced site in Database\"}";
                servicedSitesFromDatabase.add(siteFromDatabase);
            }

        //    manager.setServicedSites(null);
        manager.setServicedSites(servicedSitesFromDatabase);
        em.persist(manager);


        return "{\"status\" :  1}";
    }


    @RequestMapping(value = "update")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String update(@RequestBody Manager manager) {

        Integer priority = manager.getPriority();
        String name = manager.getName();
        String phoneNumber = manager.getPhoneNumber();
        Boolean afk = manager.getAfk();
        Boolean receiveCallInBrowser = manager.getReceiveCallInBrowser();

        Manager managerFromDatabase = managerDAO.getManagerByPhone(manager.getPhoneNumber());


        if (managerFromDatabase == null)
            return "{\"status\" :  0,  \"message\" : 'Manager don`t exist'.}";

        if (priority != null)
            managerFromDatabase.setPriority(priority);

        if (name != null)
            managerFromDatabase.setName(name);

        if (phoneNumber != null)
            managerFromDatabase.setPhoneNumber(phoneNumber);

        if (afk != null)
            managerFromDatabase.setAfk(afk);

        if (receiveCallInBrowser != null)
            managerFromDatabase.setReceiveCallInBrowser(receiveCallInBrowser);

        List<ServicedSite> servicedSites = manager.getServicedSites();
        if (servicedSites != null && !servicedSites.isEmpty()) {
            ArrayList<ServicedSite> servicedSitesFromDatabase = new ArrayList<ServicedSite>();
            for (ServicedSite site : servicedSites) {
                String domain = site.getDomain();
                ServicedSite siteFromDatabase = em.find(ServicedSite.class, domain);
                if (siteFromDatabase == null)
                    return "{\"status\" :  1, massage: \"There is no serviced site in Database\"}";
                servicedSitesFromDatabase.add(siteFromDatabase);
            }
            managerFromDatabase.setServicedSites(servicedSitesFromDatabase);
        }

        em.merge(managerFromDatabase);

        return "{\"status\" :  1}";
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String delete(@RequestBody String phoneNumber) {

        phoneNumber = phoneNumber.substring(1, phoneNumber.length() - 1);
        Manager managerFromDatabase = em.find(Manager.class, phoneNumber);

        if (managerFromDatabase == null)
            return "{\"status\" :  0,  \"message\" : 'Manager don`t exist'.}";

        em.remove(managerFromDatabase);
        return "{\"status\" :  1}";


    }


}
