package com.geerong.test.m68000;

import static com.geerong.test.m68000.Bytes.*;
import static com.geerong.test.m68000.Instructions.*;

public class Rom {

    private final byte[] rom;

    private long getInt(int offset) {
        return ((rom[offset] & 0xff) << 24) |
                ((rom[offset + 1] & 0xff) << 16) |
                ((rom[offset + 2] & 0xff) << 8) |
                (rom[offset + 3] & 0xff);
    }

    public Rom(byte[] rom) {
        this.rom = rom;
    }

    public int getBegin() {
        return (int) getInt(0x1A0);
    }

    public int getEnd() {
        return (int) getInt(0x1A4);
    }


}
