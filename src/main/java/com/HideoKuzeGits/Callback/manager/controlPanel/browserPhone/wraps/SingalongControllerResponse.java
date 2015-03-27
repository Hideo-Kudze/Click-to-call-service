package com.HideoKuzeGits.Callback.manager.controlPanel.browserPhone.wraps;

/**
 * Created by root on 17.02.15.
 */
public class SingalongControllerResponse {

    private boolean single = false;

    public SingalongControllerResponse() {
    }

    public SingalongControllerResponse(boolean single) {
        this.single = single;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }
}
