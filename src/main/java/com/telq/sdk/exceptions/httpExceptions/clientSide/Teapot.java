package com.telq.sdk.exceptions.httpExceptions.clientSide;

public class Teapot extends Exception {

    public Teapot(String message) {
        super("ERROR CODE: [418][I'm a teapot :)]: " + message);
    }

    public Teapot() {
        super("ERROR CODE: [418][I'm a teapot :)]");
    }

}
