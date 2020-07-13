package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class ApiCredentialsException extends Exception {

    public ApiCredentialsException(String message) {
        super("Error with your Api Credentials [" + message + "]");
    }

}
