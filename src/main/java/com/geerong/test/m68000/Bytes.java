package com.geerong.test.m68000;

public class Bytes {

    public static short extendSign(short b) {
        return (short) (byte) b;
    }

    public static int getLongWord(byte[] memory, int offset) {
        return ((memory[offset] & 0xff) << 24) |
                ((memory[offset + 1] & 0xff) << 16) |
                ((memory[offset + 2] & 0xff) << 8) |
                (memory[offset + 3] & 0xff);
    }

    public static int signedExtendedWord(byte[] memory, int offset) {
        return (int) (short) (((memory[offset] & 0xff) << 8) | memory[offset + 1]);
    }

    public static byte getBits1512(byte[] rom, int offset) {
        return (byte) ((rom[offset] & 0xf0) >> 4);
    }

    public static int getBits110(byte[] rom, int offset) {
        return ((rom[offset] & 0x0f) << 8) | rom[offset + 1];
    }

    public static byte getBits116(byte[] rom, int offset) {
        return (byte) (((rom[offset] & 0x0f) << 2) | ((rom[offset + 1] & 0xc0) >> 6));
    }

    public static byte getBits118(byte[] rom, int offset) {
        return (byte) (rom[offset] & 0x0f);
    }

    public static byte getBit119(byte[] rom, int offset) {
        return (byte) ((rom[offset] & 0x0e) >> 1);
    }

    public static byte getBit86(byte[] rom, int offset) {
        return (byte) (((rom[offset + 1] & 0xc0) >> 6) | ((rom[offset] & 0x01) << 2));
    }

    public static byte getBit87(byte[] rom, int offset) {
        return (byte) (((rom[offset + 1] & 0x80) >> 7) | ((rom[offset] & 0x01) << 1));
    }

    public static byte getBit8(byte[] rom, int offset) {
        return (byte) (rom[offset] & 0x01);
    }

    public static int getBit70(byte[] rom, int offset) {
        return (rom[offset + 1] & 0xff);
    }

    public static byte getBit76(byte[] rom, int offset) {
        return (byte) ((rom[offset + 1] & 0xc0) >> 6);
    }

    public static byte getBit7(byte[] rom, int offset) {
        return (byte) (rom[offset + 1] & 0x80);
    }

    public static byte getBit53(byte[] rom, int offset) {
        return (byte) ((rom[offset + 1] & 0x38) >> 3);
    }

    public static byte getBit54(byte[] rom, int offset) {
        return (byte) ((rom[offset + 1] & 0x30) >> 4);
    }

    public static byte getBit43(byte[] rom, int offset) {
        return (byte) ((rom[offset + 1] & 0x18) >> 3);
    }

    public static byte getBit20(byte[] rom, int offset) {
        return (byte) (rom[offset + 1] & 0x03);
    }

}
