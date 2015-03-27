package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone;

import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.wraps.SingalongControllerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * Created by root on 17.02.15.
 */

@Controller
public class ManagerPhoneSingletonController {

    @Autowired
    private BrowserPhoneApi onlineService;

    @MessageMapping(value = "/serviceManager/controlPanel/singletonControl")
    @SendToUser("/browserPhone/singletonControl")
    public SingalongControllerResponse isSingle(SimpMessageHeaderAccessor headerAccessor) {

        String managerPhone = headerAccessor.getUser().getName();
        boolean managerOnline = onlineService.isManagerOnline(managerPhone);
        return new SingalongControllerResponse(!managerOnline);
    }
}
