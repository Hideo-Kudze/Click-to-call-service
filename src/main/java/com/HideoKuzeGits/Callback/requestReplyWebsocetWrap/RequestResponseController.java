package com.HideoKuzeGits.Callback.requestReplyWebsocetWrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Created by root on 23.12.14.
 */
@Controller
public class RequestResponseController {

    @Autowired
    private RequestWebSocketService requestResponseService;


    @MessageMapping(value = "/serviceManager/controlPanel/{destination}")
    public void isManagerOnlineInBrowserPhone(SimpMessageHeaderAccessor headerAccessor
            , MessageWrap messageWrap
            , @DestinationVariable String destination) {

        destination = "/" + destination;
        String managerPhone = headerAccessor.getUser().getName();
        requestResponseService.response(destination, messageWrap);
    }


}
