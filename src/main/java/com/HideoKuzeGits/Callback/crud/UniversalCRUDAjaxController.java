package com.HideoKuzeGits.Callback.crud;

import com.HideoKuzeGits.Callback.registration.ActiveUser;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


/**
 * Created by root on 15.10.14.
 */

@Controller
@Transactional
public class UniversalCRUDAjaxController {

    private static Logger log = Logger.getLogger(UniversalCRUDAjaxController.class.getName());

    @Autowired
    @Qualifier(value = "universalCRUDService")
    private UniversalCRUDService universalCRUDService;


    @RequestMapping(value = "domainObject/{method}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String create(@ActiveUser User activeUser, @PathVariable("method") String method, @RequestBody String json) {

        try {
            UniversalCRUDService crudService = universalCRUDService.getRealisationForType(json);

            if (method.equals("create"))
                crudService.create(activeUser, json);

            else if (method.equals("read"))
                return new Gson().toJson(crudService.read(activeUser, json));

            else if (method.equals("update"))
                crudService.update(activeUser, json);

            else if (method.equals("delete"))
                crudService.delete(activeUser, json);

        } catch (Exception e) {
            log.warn("Create exception.", e);
            return new StatusResponse(0, e.getMessage()).toString();
        }

        return new StatusResponse(1).toString();
    }


}
