package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class MethodNotAllowed extends Exception {

    public MethodNotAllowed(String message) {
        super("ERROR CODE: [405][Method Not Allowed -- You tried to access a address with an invalid method.]: " + message);
    }

    public MethodNotAllowed() {
        super("ERROR CODE: [405][Method Not Allowed -- You tried to access a address with an invalid method.]");
    }
}
