package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone;

import com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.wraps.MakeCallInfo;
import com.HideoKuzeGits.Callback.requestReplyWebsocetWrap.RequestWebSocketService;
import com.HideoKuzeGits.Callback.requestReplyWebsocetWrap.WebSocketTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Created by root on 23.12.14.
 */
@Service
public class BrowserPhoneApi {

    private static Logger log = Logger.getLogger(BrowserPhoneApi.class.getName());

    @Autowired
    private RequestWebSocketService requestService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    public boolean isManagerOnline(String managerNumber) {

        try {
            requestService.request(managerNumber, "/ping", 3500l);
            return true;
        } catch (WebSocketTimeoutException e) {
            return false;
        }
    }

    public void makeCall(String managerNumber, String domain) {
        simpMessagingTemplate.convertAndSendToUser(managerNumber, "/browserPhone/makeCall",
                new MakeCallInfo("+380630405349", domain));
    }

    public void stopCall(String managerNumber) {
        simpMessagingTemplate.convertAndSendToUser(managerNumber, "/browserPhone/stopCall", "NULL");
    }


}
