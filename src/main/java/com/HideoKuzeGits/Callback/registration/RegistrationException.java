package com.HideoKuzeGits.Callback.registration;

/**
 * Created by root on 09.10.14.
 */
public class RegistrationException extends Exception {


    private int code;
    private String message;

    public RegistrationException(int code, String message) {

        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
