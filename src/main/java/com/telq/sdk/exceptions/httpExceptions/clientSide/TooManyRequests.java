package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class TooManyRequests extends Exception {

    public TooManyRequests(String message) {
        super("ERROR CODE: [429][Too Many Requests -- You're requesting too many addresses! Slow down!]: " + message);
    }

    public TooManyRequests() {
        super("ERROR CODE: [429][Too Many Requests -- You're requesting too many addresses! Slow down!]");
    }
}
