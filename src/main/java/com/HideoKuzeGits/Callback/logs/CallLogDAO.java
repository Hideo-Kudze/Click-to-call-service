package com.HideoKuzeGits.Callback.logs;

import com.HideoKuzeGits.Callback.domain.CallLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by root on 02.03.15.
 */

@Controller
public class CallLogDAO {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/serviceManager/getCallLogByManagerNumber",
            method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<CallLog> getLogsByManagerNumber(@RequestBody CallLogRequest callLogRequest) {

        TypedQuery<CallLog> query
                = em.createQuery("SELECT l FROM CallLog l where l.managerNumber = :managerNumber", CallLog.class);
        query.setParameter("managerNumber", callLogRequest.getPhoneNumber());
        return query.getResultList();
    }
}
