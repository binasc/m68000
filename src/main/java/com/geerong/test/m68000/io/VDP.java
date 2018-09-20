package com.geerong.test.m68000.io;

import com.geerong.test.m68000.exception.IllegalVisitException;

public class VDP implements IODevice {

    private static final int DATA_PORT = 0xc00000;

    private static final int DATA_PORT_MIRROR = 0xc00002;

    private static final int CONTROL_PORT = 0xc00004;

    private static final int CONTROL_PORT_MIRROR = 0xc00006;

    private short status = 0x3601;

    @Override
    public int[] ports() {
        return new int[] { DATA_PORT, DATA_PORT_MIRROR, CONTROL_PORT, CONTROL_PORT_MIRROR };
    }

    @Override
    public int readWord(int start) {
        if (start == CONTROL_PORT || start == CONTROL_PORT_MIRROR) {
            return status;
        }
        throw new IllegalVisitException(start);
    }

    @Override
    public long readLongWord(int start) {
        throw new IllegalVisitException(start);
    }

}
