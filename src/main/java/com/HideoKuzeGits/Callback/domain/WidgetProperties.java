package com.HideoKuzeGits.Callback.domain;

import com.HideoKuzeGits.Callback.crud.Updatable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by root on 15.12.14.
 */

@Entity
public class WidgetProperties extends AbstractUserOwn {

    @Id
    private String id;

    @Updatable
    private boolean showCallBackWidget = false;
    @Updatable
    private boolean showOnlineConsultant = false;
    @Updatable
    private boolean showPhoneButton = false;
    @Updatable
    private int secondsBeforeButtonShows = 25;
    @Updatable
    private int secondsBeforeFormShows = 25;
    @Updatable
    private int secondsBeforeDrawingAttentionToTheInactiveTab = 25;
    @Updatable
    private boolean drawingAttentionToTheInactiveTab = false;
    @Updatable
    private boolean showOncePerDay = false;
    @Updatable
    private boolean showOnceForOneUser = false;
    @Updatable
    private boolean interceptDesireToLive = false;
    @Updatable
    private boolean sendSms = false;

    public WidgetProperties() {
    }

    public boolean isShowCallBackWidget() {
        return showCallBackWidget;
    }

    public void setShowCallBackWidget(boolean callBackWidgetOn) {
        this.showCallBackWidget = callBackWidgetOn;
    }

    public boolean isShowOnlineConsultant() {
        return showOnlineConsultant;
    }

    public void setShowOnlineConsultant(boolean showOnlineConsultant) {
        this.showOnlineConsultant = showOnlineConsultant;
    }

    public boolean isShowPhoneButton() {
        return showPhoneButton;
    }

    public void setShowPhoneButton(boolean showPhoneButton) {
        this.showPhoneButton = showPhoneButton;
    }

    public int getSecondsBeforeButtonShows() {
        return secondsBeforeButtonShows;
    }

    public void setSecondsBeforeButtonShows(int secondsBeforeButtonShows) {
        this.secondsBeforeButtonShows = secondsBeforeButtonShows;
    }

    public boolean isDrawingAttentionToTheInactiveTab() {
        return drawingAttentionToTheInactiveTab;
    }

    public void setDrawingAttentionToTheInactiveTab(boolean drawingAttentionToTheInactiveTab) {
        this.drawingAttentionToTheInactiveTab = drawingAttentionToTheInactiveTab;
    }

    public int getSecondsBeforeDrawingAttentionToTheInactiveTab() {
        return secondsBeforeDrawingAttentionToTheInactiveTab;
    }

    public void setSecondsBeforeDrawingAttentionToTheInactiveTab(int secondsBeforeDrawingAttentionToTheInactiveTab) {
        this.secondsBeforeDrawingAttentionToTheInactiveTab = secondsBeforeDrawingAttentionToTheInactiveTab;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSecondsBeforeFormShows() {
        return secondsBeforeFormShows;
    }

    public void setSecondsBeforeFormShows(int secondsBeforeFormShows) {
        this.secondsBeforeFormShows = secondsBeforeFormShows;
    }

    public boolean isShowOncePerDay() {
        return showOncePerDay;
    }

    public void setShowOncePerDay(boolean showMoreThanOncePerDay) {
        this.showOncePerDay = showMoreThanOncePerDay;
    }

    public boolean isShowOnceForOneUser() {
        return showOnceForOneUser;
    }

    public void setShowOnceForOneUser(boolean showMoreThanOnceForOneUser) {
        this.showOnceForOneUser = showMoreThanOnceForOneUser;
    }

    public boolean isInterceptDesireToLive() {
        return interceptDesireToLive;
    }

    public void setInterceptDesireToLive(boolean interceptDesireToLive) {
        this.interceptDesireToLive = interceptDesireToLive;
    }

    public boolean isSendSms() {
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
        this.sendSms = sendSms;
    }

    @Override
    public void setOwner(CallbackUser owner) {
        super.setOwner(owner);
        if (owner == null)
            return;
    }
}
