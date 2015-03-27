package com.HideoKuzeGits.Callback.requestReplyWebsocetWrap;

import java.util.UUID;

/**
 * Created by root on 13.02.15.
 */
public class MessageWrap {

    private String body;
    private String id = UUID.randomUUID().toString();


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

