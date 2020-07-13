package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class InitialisationException extends Exception {

    public InitialisationException(String initClass) {
        super(initClass + " has not been initialised. Please initialised it first.");
    }

}
