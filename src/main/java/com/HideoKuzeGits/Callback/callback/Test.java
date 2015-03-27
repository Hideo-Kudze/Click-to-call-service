package com.HideoKuzeGits.Callback.callback;

import com.HideoKuzeGits.Callback.billing.BillingService;
import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.HideoKuzeGits.Callback.domain.Manager;
import com.HideoKuzeGits.Callback.domain.ServicedSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;

/**
 * Created by root on 26.11.14.
 */
@Service
public class Test {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private BillingService basicService;

    public static void main(String[] args) {
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("//home/hideo/IdeaProjects/Callback/src/main/webapp/WEB-INF/dispatcher-servlet.xml", "//home/hideo/IdeaProjects/Callback/src/main/webapp/WEB-INF/spring_hibernate.xml", "//home/hideo/IdeaProjects/Callback/src/main/webapp/WEB-INF/mail.xml");
        Test test = context.getBean(Test.class);
        test.test();

    }

    @Transactional
    public void test() {

        CallbackUser a = em.find(CallbackUser.class, "a");
        ArrayList<Manager> managers = new ArrayList<Manager>();

        Manager manager1 = new Manager("Manager1", "+380959122598");
        managers.add(manager1);
        Manager manager2 = new Manager("Manager2", "+380975523563");
        managers.add(manager2);
        Manager manager3 = new Manager("Manager3", "+380930629870");
        managers.add(manager3);
        Manager manager4 = new Manager("Manager3", "+380630405349");

        ServicedSite servicedSite = new ServicedSite("localhost", managers, "MyComputer");
        servicedSite.setCallsReceived(5);
        servicedSite.setOwner(a);
        ServicedSite servicedSite1 = new ServicedSite("localhost1", managers, "MyComputer");
        servicedSite1.setCallsReceived(7);
        servicedSite1.setOwner(a);
        ServicedSite servicedSite2 = new ServicedSite("localhost2", managers, "MyComputer");
        servicedSite2.setCallsReceived(9);
        servicedSite2.setOwner(a);


        em.persist(servicedSite);
        em.persist(servicedSite1);
        em.persist(servicedSite2);
        em.persist(manager1);
        em.persist(manager2);
        em.persist(manager3);
        em.persist(manager4);

    }
}
