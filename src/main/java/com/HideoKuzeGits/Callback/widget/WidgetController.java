package com.HideoKuzeGits.Callback.widget;

import com.HideoKuzeGits.Callback.crud.StatusResponse;
import com.HideoKuzeGits.Callback.registration.ActiveUser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


/**
 * Created by root on 15.10.14.
 */

@Controller
@RequestMapping(method = RequestMethod.POST)
public class WidgetController {


    @Autowired
    private WidgetService widgetService;

    private static Logger log = Logger.getLogger(WidgetController.class.getName());

    @RequestMapping(value = "buildWidget")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String buildWidget(@ActiveUser User user,
                              @RequestParam String servicedSiteId) {
        return widgetService.buildWidget(user, servicedSiteId);
    }

    @RequestMapping(value = "getWidgetProperties")
    @ResponseBody
    public String getWidgetProperties(@RequestParam("servicedSiteId") String servicedSiteId,
                                      HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            return widgetService.getJsonWidgetData(servicedSiteId);
        } catch (Exception e) {
            log.warn("Create exception.", e);
            return new StatusResponse(0, e.getMessage()).toString();
        }
    }


}

