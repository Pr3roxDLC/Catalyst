package com.krazzzzymonkey.catalyst.ellerton.japng.error;

/**
 * All exceptions in the library are a PngException or subclass of it.
 */
public class PngException extends Exception {
    int code;

    public PngException(int code, String message) {
        super(message);
        this.code = code;
    }

    public PngException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
