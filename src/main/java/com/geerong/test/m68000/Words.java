package com.geerong.test.m68000;

public class Words {

    public static int extendSign(int word) {
        return (int) (short) word;
    }

    public static int getBits1512(int word) {
        return (word & 0xf000) >> 12;
    }

    public static int getBits1312(int word) {
        return (word & 0x3000) >> 12;
    }

    public static int getBits110(int word) {
        return word & 0x0fff;
    }

    public static int getBits116(int word) {
        return (word & 0x0fc0) >> 6;
    }

    public static int getBits118(int word) {
        return (word & 0x0f00) >> 8;
    }

    public static int getBit119(int word) {
        return (word & 0x0e00) >> 9;
    }

    public static int getBit86(int word) {
        return (word & 0x01c0) >> 6;
    }

    public static int getBit8(int word) {
        return (word & 0x0100) >> 8;
    }

    public static int getBit70(int word) {
        return word & 0x00ff;
    }

    public static int getBit76(int word) {
        return (word & 0x00c0) >> 6;
    }

    public static int getBit53(int word) {
        return (word & 0x0038) >> 3;
    }

    public static int getBit54(int word) {
        return (word & 0x0030) >> 4;
    }

    public static int getBit43(int word) {
        return (word & 0x0018) >> 3;
    }

    public static int getBit20(int word) {
        return word & 0x0007;
    }

}
