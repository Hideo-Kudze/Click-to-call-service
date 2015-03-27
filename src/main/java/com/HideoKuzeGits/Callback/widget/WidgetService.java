package com.HideoKuzeGits.Callback.widget;

import com.HideoKuzeGits.Callback.crud.UniversalCRUDService;
import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import com.HideoKuzeGits.Callback.domain.WidgetProperties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 27.11.14.
 */

@Service
public class WidgetService {

    private static final String WIDGET = "<link rel=\"stylesheet\" href=\"//77.47.228.33:8080/resources/widget.css\"> \n" +
            "<script type=\"text/javascript\">var servicedSiteId=\"${servicedSiteId}\";</script> \n" +
            "<script type=\"text/javascript\" src=\"//77.47.228.33:8080/resources/widget.js\" charset=\"UTF-8\"></script>";
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UniversalCRUDService CRUDService;

    public String buildWidget(User user, String servicedSiteId) {

        ServicedSite servicedSite = em.find(ServicedSite.class, servicedSiteId);
        CallbackUser callbackUser = servicedSite.getOwner();
        String email = callbackUser.getEmail();
        if (!email.equals(user.getUsername()))
            throw new AccessException("Try access to widget properties of another user.");

        String id = servicedSite.getId();
        return WIDGET.replace("${servicedSiteId}", id);
    }

    public String getJsonWidgetData(String servicedSiteId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        WidgetProperties widgetProperties = getWidgetProperties(servicedSiteId);
        WidgetInfo widgetInfo = getWidgetInfo(servicedSiteId);

        JsonObject widgetData = new JsonObject();
        widgetData.add("widgetProperties", new Gson().toJsonTree(widgetProperties));
        widgetData.add("widgetInfo", new Gson().toJsonTree(widgetInfo));
        return widgetData.toString();
    }

    public WidgetProperties getWidgetProperties(String servicedSiteId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        WidgetProperties widgetProperties = em.find(WidgetProperties.class, servicedSiteId);
        CRUDService.removeCircularReferences(widgetProperties);
        CRUDService.removeExcludedFields(widgetProperties);
        return widgetProperties;
    }

    public WidgetInfo getWidgetInfo(String servicedSiteId) {


        int freeManagersCount = getFreeOrBusyManagersCount(servicedSiteId, false);
        int busyManagersCount = getFreeOrBusyManagersCount(servicedSiteId, true);
        int callsHandledToday = getCallsHandledToday(servicedSiteId);
        return new WidgetInfo(freeManagersCount, busyManagersCount, callsHandledToday);
    }


    private int getFreeOrBusyManagersCount(String servicedSiteId, boolean busy) {
        TypedQuery<Long> query
                = em.createQuery("SELECT count(m) FROM ServicedSite s  INNER JOIN s.managers m" +
                " where m.busy = :busy  and  s.id = :servicedSiteId", Long.class);
        query.setParameter("servicedSiteId", servicedSiteId);
        query.setParameter("busy", busy);
        return (int) (long) query.getSingleResult();
    }

    private int getCallsHandledToday(String servicedSiteId) {
        TypedQuery<String> domainQuery
                = em.createQuery("SELECT s.domain FROM ServicedSite s  where s.id = :servicedSiteId", String.class);
        domainQuery.setParameter("servicedSiteId", servicedSiteId);
        String domain = domainQuery.getSingleResult();
        Date currentDay = getCurentDayWithoutTime();
        TypedQuery<Long> query
                = em.createQuery("SELECT count(l) FROM CallLog l  " +
                "where l.time > :currentDay and l.domain = :domain", Long.class);
        query.setParameter("currentDay", currentDay, TemporalType.DATE);
        query.setParameter("domain", domain);
        return (int) (long) query.getSingleResult();
    }

    private Date getCurentDayWithoutTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

}
