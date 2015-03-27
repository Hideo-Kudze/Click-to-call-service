package com.HideoKuzeGits.Callback.requestReplyWebsocetWrap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by root on 13.02.15.
 */
public class RequestResponseFlow {

    private static Logger log = Logger.getLogger(RequestWebSocketService.class.getName());

    private SimpMessagingTemplate simpMessagingTemplate;

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String response;
    private boolean ended;


    public RequestResponseFlow(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public String request(String managerNumber, String destination, MessageWrap messageWrap, Long timeout) throws WebSocketTimeoutException {

        if (ended)
            throw new ReuseOfFlow("You can use instance of RequestResponseFlow only once.");

        try {
            simpMessagingTemplate.convertAndSendToUser(managerNumber
                    , "/browserPhone" + destination
                    , messageWrap);
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (response == null) {
            log.info("Timeout rich for request to destination" + destination + " .");
            throw new WebSocketTimeoutException("Don`t get response from browser phone");
        }

        ended = true;
        return response;
    }

    public void response(String response) {

        this.response = response;
        countDownLatch.countDown();
    }

}
