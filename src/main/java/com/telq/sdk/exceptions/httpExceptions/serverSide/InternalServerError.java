package com.telq.sdk.exceptions.httpExceptions.serverSide;

public class InternalServerError extends Exception {

    public InternalServerError(String message) {
        super("ERROR CODE: [500][Internal Server Error -- We had a problem with our server. Try again later.]: " + message);
    }

    public InternalServerError() {
        super("ERROR CODE: [500][Internal Server Error -- We had a problem with our server. Try again later.]");
    }
}
