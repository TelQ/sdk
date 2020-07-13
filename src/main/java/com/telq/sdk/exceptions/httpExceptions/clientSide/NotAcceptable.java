package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class NotAcceptable extends Exception {

    public NotAcceptable(String message) {
        super("ERROR CODE: [406][Not Acceptable -- You requested a format that isn't json.]: " + message);
    }

    public NotAcceptable() {
        super("ERROR CODE: [406][Not Acceptable -- You requested a format that isn't json.]");
    }

}
