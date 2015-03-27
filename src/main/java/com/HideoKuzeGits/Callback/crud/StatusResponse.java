package com.HideoKuzeGits.Callback.crud;

import com.google.gson.Gson;

/**
 * Created by root on 19.10.14.
 */
public class StatusResponse {

    private Integer status;
    private String message;

    public StatusResponse() {
    }

    public StatusResponse(Integer status) {
        this.status = status;
    }

    public StatusResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
