package com.HideoKuzeGits.Callback.consultant.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;

import javax.persistence.*;

/**
 * Created by root on 09.03.15.
 */

@Embeddable
public class Message {

    private Long time;
    @ManyToOne(fetch = FetchType.EAGER)
    private Addressee addressee;
    private String text;

    @Transient
    private String chatId;

    @Transient
    private String userId;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Addressee getAddressee() {
        return addressee;
    }

    public void setAddressee(Addressee addressee) {
        this.addressee = addressee;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
