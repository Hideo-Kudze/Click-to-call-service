package com.HideoKuzeGits.Callback.consultant.domain;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.domain.AbstractUserOwn;

import javax.persistence.*;
import java.util.List;

/**
 * Created by root on 09.03.15.
 */

@Entity
public class History extends AbstractUserOwn{

    @Id
    private String id = UsefulStaticMethods.generateUUID();
    @ManyToOne(fetch = FetchType.EAGER)
    private UserInformation userInformation;
    @ElementCollection
    @OrderColumn
    private List<Message> messages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
