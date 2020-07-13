package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class Forbidden extends Exception{

    public Forbidden(String message) {
        super("ERROR CODE: [403][Forbidden -- The address requested is hidden for administrators only.]: " + message);
    }

    public Forbidden() {
        super("ERROR CODE: [403][Forbidden -- The address requested is hidden for administrators only.]");
    }
}
