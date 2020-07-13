package com.telq.sdk.exceptions.httpExceptions.serverSide;

public class ServiceUnavailable extends Exception {

    public ServiceUnavailable(String message) {
        super("ERROR CODE: [503][Service Unavailable -- We're temporarily offline for maintenance. Please try again later.]: " + message);
    }

    public ServiceUnavailable() {
        super("ERROR CODE: [503][Service Unavailable -- We're temporarily offline for maintenance. Please try again later.]");
    }
}
