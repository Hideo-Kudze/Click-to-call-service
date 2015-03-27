package com.HideoKuzeGits.Callback.requestReplyWebsocetWrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Created by root on 23.12.14.
 */
@Service
public class RequestWebSocketService {

    private static Logger log = Logger.getLogger(RequestWebSocketService.class.getName());

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private ApplicationContext applicationContext;

    private RequestsMap<RequestResponseFlow> requestsMap = new RequestsMap<RequestResponseFlow>();


    public String request(String userName, String destination) throws WebSocketTimeoutException {

        return request(userName, destination, 2500l);
    }

    public String request(String userName, String destination, Long timeout) throws WebSocketTimeoutException {

        log.info("Request for manager with number "
                + userName + "to destination " + destination + ".");

        RequestResponseFlow requestResponseFlow = new RequestResponseFlow(simpMessagingTemplate);
        MessageWrap messageWrap = new MessageWrap();
        String id = messageWrap.getId();

        requestsMap.put(destination, id, requestResponseFlow);

        String response = requestResponseFlow.request(userName, destination, messageWrap, timeout);
        requestsMap.remove(destination, id);

        log.info("Response in time from destination " + destination
                + ". With body " + response + ".");

        return response;
    }


    protected void response(String destination, MessageWrap messageWrap) {

        log.info("Response from destination " + destination
                + ". With body " + messageWrap.getBody() + ".");

        String id = messageWrap.getId();
        String body = messageWrap.getBody();

        RequestResponseFlow requestResponseFlow = requestsMap.get(destination, id);
        if (body == null) body = "";
        requestResponseFlow.response(body);
    }

}
