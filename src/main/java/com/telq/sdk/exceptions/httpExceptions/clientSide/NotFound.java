package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class NotFound extends Exception {

    public NotFound(String message) {
        super("ERROR CODE: [404][Not Found -- The specified address could not be found.]: " + message);
    }

    public NotFound() {
        super("ERROR CODE: [404][Not Found -- The specified address could not be found.]");
    }
}
