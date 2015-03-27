package com.HideoKuzeGits.Callback.widget;

/**
 * Created by root on 15.01.15.
 */
public class WidgetInfo {

    private int managersFree;
    private int managesBusy;
    private int callsHandledToday;

    public WidgetInfo() {
    }

    public WidgetInfo(int managersFree, int managesBusy, int callsHandledToday) {
        this.managersFree = managersFree;
        this.managesBusy = managesBusy;
        this.callsHandledToday = callsHandledToday;
    }

    public int getManagersFree() {
        return managersFree;
    }

    public void setManagersFree(int managersFree) {
        this.managersFree = managersFree;
    }

    public int getManagesBusy() {
        return managesBusy;
    }

    public void setManagesBusy(int managesBusy) {
        this.managesBusy = managesBusy;
    }

    public int getCallsHandledToday() {
        return callsHandledToday;
    }

    public void setCallsHandledToday(int callsHandledToday) {
        this.callsHandledToday = callsHandledToday;
    }
}
