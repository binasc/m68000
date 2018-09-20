package com.geerong.test.m68000.io;

import com.geerong.test.m68000.exception.IllegalVisitException;

public class Controller implements IODevice {

    private static final int CTRL1 = 0xa10008;
    private static final int CTRL2 = 0xa1000a;

    @Override
    public int[] ports() {
        return new int[] { CTRL1, CTRL2 };
    }

    @Override
    public int readWord(int start) {
        if (start == CTRL1 || start == CTRL2) {
            return 1;
        }
        throw new IllegalVisitException(CTRL1);
    }

    @Override
    public long readLongWord(int start) {
        if (start == CTRL1) {
            return 0x00010001;
        }
        throw new IllegalVisitException(CTRL1);
    }

}
