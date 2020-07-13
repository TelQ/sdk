package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class Unauthorized extends Exception {

    public Unauthorized(String message) {
        super("ERROR CODE: [401][Unauthorized -- Your API key is wrong.]: " + message);
    }

    public Unauthorized() {
        super("ERROR CODE: [401][Unauthorized -- Your API key is wrong.]");
    }
}
