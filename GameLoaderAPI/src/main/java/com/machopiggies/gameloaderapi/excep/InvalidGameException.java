package com.machopiggies.gameloaderapi.excep;

public class InvalidGameException extends Exception {
    public InvalidGameException() {
        super();
    }

    public InvalidGameException(String reason) {
        super(reason);
    }

    public InvalidGameException(String reason, Throwable e) {
        super(reason, e);
    }
}