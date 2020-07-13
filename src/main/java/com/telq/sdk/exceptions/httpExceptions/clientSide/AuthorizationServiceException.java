package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class AuthorizationServiceException extends Exception {

    public AuthorizationServiceException(String message) {
        super("Error with your authorization service [" + message + "]");
    }

}
