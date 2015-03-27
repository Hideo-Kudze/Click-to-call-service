package com.HideoKuzeGits.Callback.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 09.10.14.
 */

@Service
@EnableScheduling
public class ScheduledUserCleaner {

    @Autowired
    private UserService userService;


    public static final int expirationDays = 1;

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    public void removeOldRegistrationRequests() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date lastRegistrationDate = calendar.getTime();
        userService.removeRegistrationRequestsOlderThen(lastRegistrationDate);
        userService.removeRecoverPasswordRequestOlderThen(lastRegistrationDate);
    }

}
