package com.geerong.test.m68000.io;

public interface IODevice {

    int[] ports();

    int readWord(int start);

    long readLongWord(int start);

}
