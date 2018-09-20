package com.geerong.test.m68000;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

@Getter
@Setter
public class Context {

    private Logger log;

    private int[] addressRegisters = new int[8];

    private int[] dataRegisters = new int[8];

    private int pc = 0x0;

    private int ccr = 0x0;

    private Memory memory = new Memory();

    public void addPc(int offset) {
        pc += offset;
    }

    public void push(long value) {
        addressRegisters[7] -= 4;
        memory.writeLongWord(addressRegisters[7], value);
    }

    public long pop() {
        long ret = memory.readLongWord(addressRegisters[7]);
        addressRegisters[7] += 4;
        return ret;
    }

    public void setExtend() {
        ccr |= 0x10;
    }

    public void setNegative() {
        ccr |= 0x08;
    }

    public void setZero() {
        ccr |= 0x04;
    }

    public void setOverflow() {
        ccr |= 0x02;
    }

    public void setCarry() {
        ccr |= 0x01;
    }

    public void clrExtend() {
        ccr &= 0x0f;
    }

    public void clrNegative() {
        ccr &= 0x17;
    }

    public void clrZero() {
        ccr &= 0x1b;
    }

    public void clrOverflow() {
        ccr &= 0x1d;
    }

    public void clrCarry() {
        ccr &= 0x1e;
    }

    public boolean extendSet() {
        return (ccr & 0x10) != 0;
    }

    public boolean negativeSet() {
        return (ccr & 0x08) != 0;
    }

    public boolean zeroSet() {
        return (ccr & 0x04) != 0;
    }

    public boolean overflowSet() {
        return (ccr & 0x02) != 0;
    }

    public boolean carrySet() {
        return (ccr & 0x01) != 0;
    }

    public String debugCCR() {
        return "X: " + (extendSet() ? "1" : "0")
                + ", N: " + (negativeSet() ? "1" : "0")
                + ", Z: " + (zeroSet() ? "1" : "0")
                + ", V: " + (overflowSet() ? "1" : "0")
                + ", C: " + (overflowSet() ? "1" : "0");
    }

}
