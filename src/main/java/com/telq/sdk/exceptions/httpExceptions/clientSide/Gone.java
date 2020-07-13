package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class Gone extends Exception {

    public Gone(String message) {
        super("ERROR CODE: [410][The address requested has been removed from our servers.]: " + message);
    }

    public Gone() {
        super("ERROR CODE: [410][The address requested has been removed from our servers.]");
    }

}
