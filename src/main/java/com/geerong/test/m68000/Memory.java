package com.geerong.test.m68000;

import com.geerong.test.m68000.exception.IllegalVisitException;
import com.geerong.test.m68000.io.Controller;
import com.geerong.test.m68000.io.IODevice;
import com.geerong.test.m68000.io.VDP;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class Memory {

    private int romOffset = 0;

    private byte[] rom = new byte[0x400000];

    private int memoryOffset = 0xFF0000;

    private byte[] memory = new byte[0x10000];

    private Map<Integer, IODevice> devices = new HashMap<>();

    public Memory() {
        Controller controller = new Controller();
        for (int port : controller.ports()) {
            devices.put(port, controller);
        }
        VDP vdp = new VDP();
        for (int port : vdp.ports()) {
            devices.put(port, vdp);
        }
    }

    public void setRom(byte[] rom, int offset, int length) {
        System.arraycopy(rom, offset, this.rom, 0, length);
    }

    public short readByte(int start) {
        start &= 0x00ffffff;
        if (start >= romOffset && start < romOffset + rom.length) {
            return (short) (rom[start - romOffset] & 0xff);
        }
        if (start >= memoryOffset && start < memoryOffset + memory.length) {
            return (short) (memory[start - memoryOffset] & 0xff);
        }
        throw new IllegalVisitException(start);
    }

    public int readWord(int start) {
        start &= 0x00ffffff;
        if (start >= romOffset && start + 1 < romOffset + rom.length) {
            return ((rom[start - romOffset] & 0xff) << 8) | (rom[start - romOffset + 1] & 0xff);
        }
        if (start >= memoryOffset && start + 1 < memoryOffset + memory.length) {
            return ((memory[start - memoryOffset] & 0xff) << 8) | (memory[start - memoryOffset + 1] & 0xff);
        }
        IODevice device = devices.get(start);
        if (device == null) {
            throw new IllegalVisitException(start);
        }
        return device.readWord(start);
    }

    public long readLongWord(int start) {
        start &= 0x00ffffff;
        if (start >= romOffset && start + 3 < romOffset + rom.length) {
            return ((rom[start - romOffset] & 0xff) << 24) |
                    ((rom[start - romOffset + 1] & 0xff) << 16) |
                    ((rom[start - romOffset + 2] & 0xff) << 8) |
                    (rom[start - romOffset + 3] & 0xff);
        }
        if (start >= memoryOffset && start + 3 < memoryOffset + memory.length) {
            return ((memory[start - memoryOffset] & 0xff) << 24) |
                    ((memory[start - memoryOffset + 1] & 0xff) << 16) |
                    ((memory[start - memoryOffset + 2] & 0xff) << 8) |
                    (memory[start - memoryOffset + 3] & 0xff);
        }

        IODevice device = devices.get(start);
        if (device == null) {
            throw new IllegalVisitException(start);
        }
        return device.readLongWord(start);
    }

    public void writeByte(int start, long value) {
        start &= 0x00ffffff;
        value &= 0xff;
        if (start >= memoryOffset && start < memoryOffset + memory.length) {
            memory[start - memoryOffset] = (byte) (value & 0xffL);
            return;
        }
        throw new IllegalVisitException(start);
    }

    public void writeWord(int start, long value) {
        start &= 0x00ffffff;
        value &= 0xffff;
        if (start >= memoryOffset && start + 1 < memoryOffset + memory.length) {
            memory[start - memoryOffset] = (byte) ((value & 0xff00L) >> 8);
            memory[start - memoryOffset + 1] = (byte) (value & 0x00ffL);
            return;
        }
        throw new IllegalVisitException(start);
    }

    public void writeLongWord(int start, long value) {
        start &= 0x00ffffff;
        value &= 0xffffffffL;
        if (start >= memoryOffset && start + 3 < memoryOffset + memory.length) {
            memory[start - memoryOffset] = (byte) ((value & 0xff000000L) >> 24);
            memory[start - memoryOffset + 1] = (byte) ((value & 0x00ff0000L) >> 16);
            memory[start - memoryOffset + 2] = (byte) ((value & 0x0000ff00L) >> 8);
            memory[start - memoryOffset + 3] = (byte) (value & 0x000000ffL);
            return;
        }
        throw new IllegalVisitException(start);
    }

}
