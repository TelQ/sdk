package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class BadRequest extends Exception {

    public BadRequest(String message) {
        super("ERROR CODE: [400][Bad Request -- Your request is invalid.]: " + message);
    }

    public BadRequest() {
        super("ERROR CODE: [400][Bad Request -- Your request is invalid.]");
    }

}
