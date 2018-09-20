package com.geerong.test.m68000.exception;

public class IllegalVisitException extends RuntimeException {

    public IllegalVisitException(int address) {
        super("address: 0x" + Integer.toHexString(address));
    }

}
