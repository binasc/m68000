package com.geerong.test.m68000;

import com.geerong.test.m68000.exception.BadOpCodeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.geerong.test.m68000.Words.*;

@Slf4j
public class Instructions {

    private static int INSTRUCTION_LENGTH = 2;

    private static int WORD_DISPLACEMENT_LENGTH = 2;

    private static int LONG_DISPLACEMENT_LENGTH = 4;

    private static int BIT_NUMBER_FIELD_LENGTH = 2;

    private static int BYTE_DATA_LENGTH = 2;

    private static int WORD_DATA_LENGTH = 2;

    private static int LONG_DATA_LENGTH = 4;

    private static int REGISTER_MASK_LIST_LENGTH = 2;

    private static int handle0000(Context ctx) {
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
        if (b8 == 1 && b53 == 1) {
            return movep(ctx);
        }
        int b119 = getBit119(ctx.getMemory().readWord((ctx.getPc())));
        if (b8 == 1 || b119 == 4) {
            int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
            switch (b76) {
                case 0:
                    return btst(ctx);
                case 1:
                    return bchg(ctx);
                case 2:
                    return bclr(ctx);
                case 3:
                    return bset(ctx);
            }
        }
        switch (b119) {
            case 0:
                return ori(ctx);
            case 1:
                return andi(ctx);
            case 2:
                return subi(ctx);
            case 3:
                return addi(ctx);
            case 5:
                return eori(ctx);
            case 6:
                return cmpi(ctx);
        }
        return 0;
    }

    private static int handle00xx(Context ctx) {
        int b86 = getBit86(ctx.getMemory().readWord((ctx.getPc())));
        if (b86 == 1) {
            return movea(ctx);
        } else {
            return move(ctx);
        }
    }

    private static int handle0100(Context ctx) {
        int b118 = getBits118(ctx.getMemory().readWord((ctx.getPc())));
        switch (b118) {
            case 0: {
                int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
                if (b76 == 3) {
                    return moveFromSR(ctx);
                } else {
                    return negx(ctx);
                }
            }
            case 2:
                return clr(ctx);
            case 4: {
                int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
                if (b76 == 3) {
                    return moveToCCR(ctx);
                } else {
                    return neg(ctx);
                }
            }
            case 6: {
                int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
                if (b76 == 3) {
                    return moveToSR(ctx);
                } else {
                    return not(ctx);
                }
            }
            case 8: {
                int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
                int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
                if ((b76 & 0x2) == 2 && b53 == 0) {
                    return ext(ctx);
                }
                if (b76 == 0) {
                    return nbcd(ctx);
                }
                if (b76 == 1) {
                    if (b53 == 0) {
                        return swap(ctx);
                    } else {
                        return pea(ctx);
                    }
                }

            }
        }
        int b110 = getBits110(ctx.getMemory().readWord((ctx.getPc())));
        if (b110 == 0x0afc) {
            return illegal(ctx);
        }
        if (b118 == 0x0a) {
            int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
            if (b76 == 3) {
                return tas(ctx);
            } else {
                return tst(ctx);
            }
        }
        int b116 = getBits116(ctx.getMemory().readWord((ctx.getPc())));
        if (b116 == 0x39) {
            int b54 = getBit54(ctx.getMemory().readWord((ctx.getPc())));
            int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
            if (b54 == 0) {
                return trap(ctx);
            }
            if (b53 == 2) {
                return link(ctx);
            }
            if (b53 == 3) {
                return unlk(ctx);
            }
            if (b54 == 2) {
                return moveusp(ctx);
            }
        }
        if (b110 == 0x0e70) {
            return reset(ctx);
        }
        if (b110 == 0x0e71) {
            return nop(ctx);
        }
        if (b110 == 0x0e72) {
            return stop(ctx);
        }
        if (b110 == 0x0e73) {
            return rte(ctx);
        }
        if (b110 == 0x0e75) {
            return rts(ctx);
        }
        if (b110 == 0x0e76) {
            return trapv(ctx);
        }
        if (b110 == 0x0e77) {
            return rtr(ctx);
        }

        if (b116 == 0x3a) {
            return jsr(ctx);
        }
        if (b116 == 0x3b) {
            return jmp(ctx);
        }

        int b86 = getBit86(ctx.getMemory().readWord((ctx.getPc())));
        int b119 = getBit119(ctx.getMemory().readWord((ctx.getPc())));
        if ((b119 & 0x05) == 0x04 && (b86 & 0x06) == 0x02) {
            return movem(ctx);
        }
        if (b86 == 7) {
            return lea(ctx);
        }
        if (b86 == 6) {
            return chk(ctx);
        }
        return 0;
    }

    private static int handle0101(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        if (b76 != 3) {
            int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
            if (b8 == 0) {
                return addq(ctx);
            } else {
                return subq(ctx);
            }
        } else {
            int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
            if (b53 != 1) {
                return scc(ctx);
            } else {
                return dbcc(ctx);
            }
        }
    }

    private static int handle0110(Context ctx) {
        int b118 = getBits118(ctx.getMemory().readWord((ctx.getPc())));
        if (b118 == 0) {
            return bra(ctx);
        }
        if (b118 == 1) {
            return bsr(ctx);
        }
        return bcc(ctx);
    }

    private static int handle0111(Context ctx) {
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        if (b8 == 0) {
            return moveq(ctx);
        }
        return 0;
    }

    private static int handle1000(Context ctx) {
        int b86 = getBit86(ctx.getMemory().readWord((ctx.getPc())));
        if (b86 == 3) {
            return divu(ctx);
        }
        if (b86 == 7) {
            return divs(ctx);
        }
        if (b86 == 8) {
            int b54 = getBit54(ctx.getMemory().readWord((ctx.getPc())));
            if (b54 == 0) {
                return sbcd(ctx);
            }
        }
        return or(ctx);
    }

    private static int handle1001(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        if (b76 != 3) {
            int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
            int b54 = getBit54(ctx.getMemory().readWord((ctx.getPc())));
            if (b8 != 1 || b54 != 0) {
                return sub(ctx);
            } else {
                return subx(ctx);
            }
        }
        return suba(ctx);
    }

    private static int handle1011(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        if (b76 != 3) {
            int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
            int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
            if (b8 == 1) {
                if (b53 != 1) {
                    return eor(ctx);
                } else {
                    return cmpm(ctx);
                }
            } else {
                return cmp(ctx);
            }
        } else {
            return cmpa(ctx);
        }
    }

    private static int handle1100(Context ctx) {
        int b86 = getBit86(ctx.getMemory().readWord(ctx.getPc()));
        if (b86 == 3) {
            return mulu(ctx);
        }
        if (b86 == 7) {
            return muls(ctx);
        }
        int b54 = getBit54(ctx.getMemory().readWord((ctx.getPc())));
        if (b86 == 8 && b54 == 0) {
            return abcd(ctx);
        }
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        if (b8 == 1 && b54 == 0) {
            return exg(ctx);
        }
        return and(ctx);
    }

    private static int handle1101(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        if (b76 != 3) {
            int b54 = getBit54(ctx.getMemory().readWord((ctx.getPc())));
            int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
            if (b8 != 1 || b54 != 0) {
                return add(ctx);
            } else {
                return addx(ctx);
            }
        } else {
            return adda(ctx);
        }
    }

    private static int handle1110(Context ctx) {
        int b119 = getBit119(ctx.getMemory().readWord((ctx.getPc())));
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        int b43 = getBit43(ctx.getMemory().readWord((ctx.getPc())));
        if (b76 == 3 && b119 == 0 || b76 != 3 && b43 == 0) {
            return asd(ctx);
        }
        if (b76 == 3 && b119 == 1 || b76 != 3 && b43 == 1) {
            return lsd(ctx);
        }
        if (b76 == 3 && b119 == 2 || b76 != 3 && b43 == 2) {
            return roxd(ctx);
        }
        if (b76 == 3 && b119 == 3 || b76 != 3 && b43 == 3) {
            return rod(ctx);
        }
        return 0;
    }

    private static int ori(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
        int b20 = getBit20(ctx.getMemory().readWord((ctx.getPc())));
        if (b53 == 7 && b20 == 4 && b76 == 0) {
            log.info("ori to ccr");
        }
        if (b53 == 7 && b20 == 4 && b76 == 1) {
            log.info("ori to sr");
        }
        log.info("ori");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int andi(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
        int b20 = getBit20(ctx.getMemory().readWord((ctx.getPc())));
        if (b53 == 7 && b20 == 4 && b76 == 0) {
            log.info("andi to ccr");
        }
        if (b53 == 7 && b20 == 4 && b76 == 1) {
            log.info("andi to sr");
        }
        log.info("andi");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int subi(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        log.info("subi");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int addi(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        log.info("addi");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int eori(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        int b53 = getBit53(ctx.getMemory().readWord((ctx.getPc())));
        int b20 = getBit20(ctx.getMemory().readWord((ctx.getPc())));
        if (b53 == 7 && b20 == 4 && b76 == 0) {
            log.info("eori to ccr");
        }
        if (b53 == 7 && b20 == 4 && b76 == 1) {
            log.info("eori to sr");
        }
        log.info("eori");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int cmpi(Context ctx) {
        int b76 = getBit76(ctx.getMemory().readWord((ctx.getPc())));
        log.info("cmpi");
        if (b76 == 0) {
            return INSTRUCTION_LENGTH + BYTE_DATA_LENGTH;
        }
        if (b76 == 1) {
            return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
        }
        if (b76 == 2) {
            return INSTRUCTION_LENGTH + LONG_DATA_LENGTH;
        }
        return 0;
    }

    private static int btst(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b8 = getBit8(instruction);
        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        OpSize opSize;
        if (b53 == 0 || b53 == 1) {
            opSize = OpSize.LONGWORD;
        } else {
            opSize = OpSize.BYTE;
        }
        ResolvedAddress dst;
        int bitNumber;
        if (b8 == 0) {
            int b70 = getBit70(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH));
            dst = parseAddressMode(ctx, b53, b20, opSize);
            log.info("{} btst #{}, {}", String.format("%08x", pc), b70, dst.describer.get());
            bitNumber = b70;
        } else {
            int b119 = getBit119(instruction);
            dst = parseAddressMode(ctx, b53, b20, opSize);
            log.info("{} btst D{}, {}", String.format("%08x", pc), b119, dst.describer.get());
            bitNumber = ctx.getDataRegisters()[b119];
        }
        log.info("    {}", ctx.debugCCR());
        if (((0x1 << bitNumber) & dst.reader.get()) == 0) {
            ctx.setZero();
        } else {
            ctx.clrZero();
        }
        log.info("    {}", ctx.debugCCR());
        int length = (b8 == 0 ? INSTRUCTION_LENGTH + BIT_NUMBER_FIELD_LENGTH : INSTRUCTION_LENGTH);
        length += dst.extLen;
        ctx.addPc(length);
        return length;
    }

    private static int bchg(Context ctx) {
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        log.info("bchg");
        return b8 == 0 ? INSTRUCTION_LENGTH + BIT_NUMBER_FIELD_LENGTH : INSTRUCTION_LENGTH;
    }

    private static int bclr(Context ctx) {
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        log.info("bclr");
        return b8 == 0 ? INSTRUCTION_LENGTH + BIT_NUMBER_FIELD_LENGTH : INSTRUCTION_LENGTH;
    }

    private static int bset(Context ctx) {
        int b8 = getBit8(ctx.getMemory().readWord((ctx.getPc())));
        log.info("bset");
        return b8 == 0 ? INSTRUCTION_LENGTH + BIT_NUMBER_FIELD_LENGTH : INSTRUCTION_LENGTH;
    }

    private static int movep(Context ctx) {
        log.info("movep");
        return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
    }

    private static int movea(Context ctx) {
        log.info("movea");
        return INSTRUCTION_LENGTH;
    }

    private static int move(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b1312 = getBits1312(instruction);
        OpSize opSize = OpSize.fromSpecial2Bits(b1312);

        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        ResolvedAddress src = parseAddressMode(ctx, b53, b20, opSize);

        int b86 = getBit86(instruction);
        int b119 = getBit119(instruction);
        ResolvedAddress dst = parseAddressMode(ctx, b86, b119, opSize, src.extLen);

        log.info("{} move{} {} -> {}", String.format("%08x", pc), opSize.d, src.describer.get(), dst.describer.get());
        long value = src.reader.get();
        dst.writer.accept(value);
        log.info("    {}", ctx.debugCCR());
        ctx.clrOverflow();
        ctx.clrCarry();
        if (value < 0) {
            ctx.setNegative();
            ctx.clrZero();
        } else if (value == 0) {
            ctx.clrNegative();
            ctx.setZero();
        }
        log.info("    {}", ctx.debugCCR());

        ctx.addPc(INSTRUCTION_LENGTH + src.extLen + dst.extLen);
        return INSTRUCTION_LENGTH + src.extLen + dst.extLen;
    }

    private static int moveFromSR(Context ctx) {
        log.info("move from sr");
        return INSTRUCTION_LENGTH;
    }

    private static int moveToCCR(Context ctx) {
        log.info("move from ccr");
        return INSTRUCTION_LENGTH;
    }

    private static int moveToSR(Context ctx) {
        log.info("move to sr");
        return INSTRUCTION_LENGTH;
    }

    private static int negx(Context ctx) {
        log.info("negx");
        return INSTRUCTION_LENGTH;
    }

    private static int clr(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b76 = getBit76(instruction);
        OpSize opSize = OpSize.fromSimple2Bits(b76);

        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        ResolvedAddress resolved = parseAddressMode(ctx, b53, b20, opSize);
        log.info("{} clr{} {}", String.format("%08x", pc), opSize.d, resolved.describer.get());
        resolved.writer.accept(0L);

        ctx.addPc(INSTRUCTION_LENGTH + resolved.extLen);
        return INSTRUCTION_LENGTH + resolved.extLen;
    }

    private static int neg(Context ctx) {
        log.info("neg");
        return INSTRUCTION_LENGTH;
    }

    private static int not(Context ctx) {
        log.info("not");
        return INSTRUCTION_LENGTH;
    }

    private static int ext(Context ctx) {
        log.info("ext");
        return INSTRUCTION_LENGTH;
    }

    private static int nbcd(Context ctx) {
        log.info("nbcd");
        return INSTRUCTION_LENGTH;
    }

    private static int swap(Context ctx) {
        log.info("swap");
        return INSTRUCTION_LENGTH;
    }

    private static int pea(Context ctx) {
        log.info("pea");
        return INSTRUCTION_LENGTH;
    }

    private static int illegal(Context ctx) {
        log.info("illegal");
        return INSTRUCTION_LENGTH;
    }

    private static int tas(Context ctx) {
        log.info("tas");
        return INSTRUCTION_LENGTH;
    }

    private static int tst(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b76 = getBit76(instruction);
        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        OpSize opSize = OpSize.fromSimple2Bits(b76);
        ResolvedAddress resolved = parseAddressMode(ctx, b53, b20, opSize);
        log.info("{} tst{} {}", String.format("%08x", pc), opSize.d, resolved.describer.get());
        log.info("    {}", ctx.debugCCR());
        long value = resolved.reader.get();
        ctx.clrOverflow();
        ctx.clrCarry();
        if (value < 0) {
            ctx.setNegative();
            ctx.clrZero();
        } else if (value == 0) {
            ctx.clrNegative();
            ctx.setZero();
        }
        log.info("    {}", ctx.debugCCR());
        ctx.addPc(INSTRUCTION_LENGTH + resolved.extLen);
        return INSTRUCTION_LENGTH + resolved.extLen;
    }

    private static int trap(Context ctx) {
        log.info("trap");
        return INSTRUCTION_LENGTH;
    }

    private static int link(Context ctx) {
        log.info("link");
        return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
    }

    private static int unlk(Context ctx) {
        log.info("unlk");
        return INSTRUCTION_LENGTH;
    }

    private static int moveusp(Context ctx) {
        log.info("move usp");
        return INSTRUCTION_LENGTH;
    }

    private static int reset(Context ctx) {
        log.info("reset");
        return INSTRUCTION_LENGTH;
    }

    private static int nop(Context ctx) {
        int pc = ctx.getPc();
        log.info("{} nop", String.format("%08x", pc));
        ctx.addPc(INSTRUCTION_LENGTH);
        return INSTRUCTION_LENGTH;
    }

    private static int stop(Context ctx) {
        return INSTRUCTION_LENGTH + WORD_DATA_LENGTH;
    }

    private static int rte(Context ctx) {
        log.info("rte");
        return INSTRUCTION_LENGTH;
    }

    private static int rts(Context ctx) {
        int pc = ctx.getPc();
        log.info("{} rts", String.format("%08x", pc));
        ctx.setPc((int) ctx.pop());
        return INSTRUCTION_LENGTH;
    }

    private static int trapv(Context ctx) {
        log.info("trapv");
        return INSTRUCTION_LENGTH;
    }

    private static int rtr(Context ctx) {
        log.info("rtr");
        return INSTRUCTION_LENGTH;
    }

    private static int jsr(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        ResolvedAddress resolved = parseAddressMode(ctx, b53, b20, OpSize.LONGWORD);
        ctx.push(pc + INSTRUCTION_LENGTH + resolved.extLen);
        ctx.setPc((int) (long) resolved.reader.get());
        log.info("{} jsr {}", String.format("%08x", pc), resolved.describer.get());
        return INSTRUCTION_LENGTH + resolved.extLen;
    }

    private enum OpSize {

        BYTE("b", 1), WORD("w", 2), LONGWORD("l", 4);

        private String d;

        private int length;

        OpSize(String d, int length) {
            this.d = d;
            this.length = length;
        }

        static OpSize fromSimple2Bits(int bits) {
            switch (bits) {
                case 0:
                    return BYTE;
                case 1:
                    return WORD;
                case 2:
                    return LONGWORD;
            }
            throw new BadOpCodeException();
        }

        static OpSize fromSpecial2Bits(int bits) {
            switch (bits) {
                case 1:
                    return BYTE;
                case 3:
                    return WORD;
                case 2:
                    return LONGWORD;
            }
            throw new BadOpCodeException();
        }
    }

    @AllArgsConstructor
    private static class ResolvedAddress {

        private final int extLen;

        private final Supplier<Integer> lea;

        private final Supplier<Long> reader;

        private final Consumer<Long> writer;

        private final Supplier<String> describer;

    }

    private static long readMemory(Context ctx, int start, OpSize opSize) {
        switch (opSize) {
            case BYTE:
                return ctx.getMemory().readByte(start);
            case WORD:
                return ctx.getMemory().readWord(start);
            case LONGWORD:
                return ctx.getMemory().readLongWord(start);
        }
        throw new BadOpCodeException();
    }

    private static void writeMemory(Context ctx, int start, OpSize opSize, long value) {
        switch (opSize) {
            case BYTE:
                ctx.getMemory().writeByte(start, value);
                return;
            case WORD:
                ctx.getMemory().writeWord(start, value);
                return;
            case LONGWORD:
                ctx.getMemory().writeLongWord(start, value);
                return;
        }
        throw new BadOpCodeException();
    }

    private static long extendValue(long value, OpSize opSize) {
        switch (opSize) {
            case BYTE:
                return value & 0xffL;
            case WORD:
                return value & 0xffffL;
            case LONGWORD:
                return value & 0xffffffffL;
        }
        throw new BadOpCodeException();

    }

    private static int chopValue(long value, OpSize opSize) {
        switch (opSize) {
            case BYTE:
                return (int) (value & 0xffL);
            case WORD:
                return (int) (value & 0xffffL);
            case LONGWORD:
                return (int) (value & 0xffffffffL);
        }
        throw new BadOpCodeException();
    }

    private static ResolvedAddress parseAddressMode(Context ctx, int mode, int xn, OpSize opSize) {
        return parseAddressMode(ctx, mode, xn, opSize, 0);
    }

    private static ResolvedAddress parseAddressMode(Context ctx, int mode, int xn, OpSize opSize, int offset) {
        switch (mode) {
            case 0:
                return new ResolvedAddress(0,
                        () -> { throw new BadOpCodeException(); },
                        () -> extendValue((long) ctx.getDataRegisters()[xn], opSize),
                        (val) -> ctx.getDataRegisters()[xn] = chopValue(val, opSize),
                        () -> "D" + String.valueOf(xn));
            case 1:
                return new ResolvedAddress(0,
                        () -> { throw new BadOpCodeException(); },
                        () -> extendValue((long) ctx.getAddressRegisters()[xn], opSize),
                        (val) -> ctx.getAddressRegisters()[xn] = chopValue(val, opSize),
                        () -> "A" + String.valueOf(xn));
            case 2:
                return new ResolvedAddress(0,
                        () -> ctx.getAddressRegisters()[xn],
                        () -> readMemory(ctx, ctx.getAddressRegisters()[xn], opSize),
                        (val) -> { throw new BadOpCodeException(); },
                        () -> String.format("(A%d)", xn));
            case 3: {
                int address = ctx.getAddressRegisters()[xn] + opSize.length;
                return new ResolvedAddress(0,
                        () -> { throw new BadOpCodeException(); },
                        () -> readMemory(ctx, address, opSize),
                        (val) -> writeMemory(ctx, address, opSize, val),
                        () -> String.format("(A%d)+%d", xn, opSize.length));
            }
            case 4:
                log.info("-(A{})", xn);
                return new ResolvedAddress(0, () -> 0, () -> 0L, (val) -> {}, () -> "");
            case 5:
                log.info("(d16, A{})", xn);
                return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH, () -> 0, () -> 0L, (val) -> {}, () -> "");
            case 6:
                log.info("(d8, A{}, Xn)", xn);
                return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH, () -> 0, () -> 0L, (val) -> {}, () -> "");
                // TODO: ???
        }
        int pc = ctx.getPc();
        switch (xn) {
            case 2: {
                int d16 = extendSign(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH + offset));
                return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH,
                        () -> pc + INSTRUCTION_LENGTH + d16,
                        () -> extendValue(pc + INSTRUCTION_LENGTH + d16, opSize),
                        (val) -> { throw new BadOpCodeException(); },
                        () -> String.format("(0x%x[d16], PC)", d16));
            }
            case 3:
                log.info("(d8, PC, Xn)");
                return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH, () -> 0, () -> 0L, (val) -> {}, () -> "");
            case 0: {
                int d16 = extendSign(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH + offset));
                return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH,
                        () -> d16,
                        () -> readMemory(ctx, d16, opSize),
                        (val) -> writeMemory(ctx, d16, opSize, val),
                        () -> String.format("(0x%x).W", d16));
            }
            case 1: {
                int d32 = (int) ctx.getMemory().readLongWord(pc + INSTRUCTION_LENGTH + offset);
                return new ResolvedAddress(2 * WORD_DISPLACEMENT_LENGTH,
                        () -> d32,
                        () -> readMemory(ctx, d32, opSize),
                        (val) -> writeMemory(ctx, d32, opSize, val),
                        () -> String.format("(0x%x).L", d32));
            }
            case 4: {
                int word = ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH + offset);
                switch (opSize) {
                    case BYTE:
                        return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH,
                                () -> { throw new BadOpCodeException(); },
                                () -> (word & 0xffL),
                                (val) -> { throw new BadOpCodeException(); },
                                () -> String.format("#<0x%x>b", word & 0xffL));
                    case WORD:
                        return new ResolvedAddress(WORD_DISPLACEMENT_LENGTH,
                                () -> { throw new BadOpCodeException(); },
                                () -> (word & 0xffffL),
                                (val) -> { throw new BadOpCodeException(); },
                                () -> String.format("#<0x%x>w", word & 0xffffL));
                    case LONGWORD: {
                        // FIXME: not right here!!!
                        throw new RuntimeException("Bad address mode");
                        //int nextWord = ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH);
                        //return new ResolvedAddress(LONG_DISPLACEMENT_LENGTH,
                        //        () -> { throw new BadOpCodeException(); },
                        //        () -> (word & 0xffffL),
                        //        (val) -> { throw new BadOpCodeException(); },
                        //        () -> String.format("#<%s>l", word & 0xffffL));
                    }
                }
            }
        }
        throw new RuntimeException("Bad address mode");
    }

    private static int jmp(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        ResolvedAddress resolved = parseAddressMode(ctx, b53, b20, OpSize.LONGWORD);
        ctx.setPc((int) (long) resolved.reader.get());
        log.info("{} jmp {}", String.format("%08x", pc), resolved.describer.get());
        return INSTRUCTION_LENGTH + resolved.extLen;
    }

    private static int movem(Context ctx) {
        log.info("movem");
        return INSTRUCTION_LENGTH + REGISTER_MASK_LIST_LENGTH;
    }

    private static int lea(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b119 = getBit119(instruction);
        int b53 = getBit53(instruction);
        int b20 = getBit20(instruction);
        ResolvedAddress dst = parseAddressMode(ctx, b53, b20, OpSize.LONGWORD);
        log.info("{} lea {} -> A{}", String.format("%08x", pc), dst.describer.get(), b119);

        ctx.getAddressRegisters()[b119] = dst.lea.get();

        ctx.addPc(INSTRUCTION_LENGTH + dst.extLen);
        return INSTRUCTION_LENGTH + dst.extLen;
    }

    private static int chk(Context ctx) {
        log.info("chk");
        return INSTRUCTION_LENGTH;
    }

    private static int addq(Context ctx) {
        log.info("addq");
        return INSTRUCTION_LENGTH;
    }

    private static int subq(Context ctx) {
        log.info("subq");
        return INSTRUCTION_LENGTH;
    }

    private static int scc(Context ctx) {
        log.info("Scc");
        return INSTRUCTION_LENGTH;
    }

    private static int dbcc(Context ctx) {
        int pc = ctx.getPc();
        log.info("{} DBcc", String.format("%08x", pc));
        int instruction = ctx.getMemory().readWord(pc);
        int b118 = getBits118(instruction);
        boolean met = conditionMet(ctx, b118);
        if (met) {
            log.info("    B{}-", conditionM[b118]);
            ctx.setPc(pc + INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH);
        } else {
            int b20 = getBit20(instruction);
            int displacement = extendSign(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH));
            if (--ctx.getDataRegisters()[b20] == -1) {
                ctx.setPc(pc + INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH);
                log.info("    B{}+ D{} ({}[d16], PC)-", conditionM[b118], b20, displacement);
            } else {
                ctx.setPc(pc + 2 + displacement);
                log.info("    B{}+ D{} ({}[d16], PC)+", conditionM[b118], b20, displacement);
            }
        }
        return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
    }

    private static int bra(Context ctx) {
        int pc = ctx.getPc();
        log.info("{} bra", String.format("%08x", pc));
        int instruction = ctx.getMemory().readWord(pc);
        int b70 = getBit70(instruction);
        if (b70 == 0x00) {
            int displacement = extendSign(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH));
            ctx.setPc(pc + 2 + displacement);
            log.info("    BRA ({}[d16], PC)", displacement);
            return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
        } else if (b70 == 0xff) {
            throw new BadOpCodeException();
            //return INSTRUCTION_LENGTH + LONG_DISPLACEMENT_LENGTH;
        } else {
            ctx.setPc(pc + 2 + Bytes.extendSign((short) b70));
            log.info("    BRA ({}[d8], PC)", b70);
            return INSTRUCTION_LENGTH;
        }
    }

    private static int bsr(Context ctx) {
        log.info("bsr");
        int b70 = getBit70(ctx.getMemory().readWord((ctx.getPc())));
        if (b70 == 0x00) {
            return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
        }
        if (b70 == 0xff) {
            return INSTRUCTION_LENGTH + LONG_DISPLACEMENT_LENGTH;
        }
        return INSTRUCTION_LENGTH;
    }

    private static String[] conditionM = new String[] {
            "T", "F", "HI", "LS",
            "CC", "CS", "NE", "EQ",
            "VC", "VS", "PL", "MI",
            "GE", "LT", "GT", "LE"
    };

    private static boolean conditionMet(Context ctx, int condition) {
        switch (condition) {
            case 0:
                return true;
            case 1:
                return false;
            case 2:
                return !(ctx.carrySet() || ctx.zeroSet());
            case 3:
                return ctx.carrySet() || ctx.zeroSet();
            case 4:
                return !ctx.carrySet();
            case 5:
                return ctx.carrySet();
            case 6:
                return !ctx.zeroSet();
            case 7:
                return ctx.zeroSet();
            case 8:
                return !ctx.overflowSet();
            case 9:
                return ctx.overflowSet();
            case 10:
                return !ctx.negativeSet();
            case 11:
                return ctx.negativeSet();
            case 12:
                return ctx.negativeSet() && ctx.overflowSet() || !ctx.negativeSet() && !ctx.overflowSet();
            case 13:
                return ctx.negativeSet() && !ctx.overflowSet() || !ctx.negativeSet() && ctx.overflowSet();
            case 14:
                return ctx.negativeSet() && ctx.overflowSet() && !ctx.zeroSet() || !ctx.negativeSet() && !ctx.overflowSet() && !ctx.zeroSet();
            case 15:
                return ctx.zeroSet() || ctx.negativeSet() && !ctx.overflowSet() || !ctx.negativeSet() && ctx.overflowSet();
        }
        throw new BadOpCodeException();
    }

    private static int bcc(Context ctx) {
        int pc = ctx.getPc();
        log.info("{} Bcc", String.format("%08x", pc));
        int instruction = ctx.getMemory().readWord(pc);
        int b118 = getBits118(instruction);
        int b70 = getBit70(instruction);
        if (b70 == 0x00) {
            int displacement = extendSign(ctx.getMemory().readWord(pc + INSTRUCTION_LENGTH));
            boolean met = conditionMet(ctx, b118);
            if (met) {
                ctx.addPc(INSTRUCTION_LENGTH + displacement);
            } else {
                ctx.addPc(INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH);
            }
            log.info("    B{} ({}[d16], PC){}", conditionM[b118], displacement, met ? "+" : "-");
            return INSTRUCTION_LENGTH + WORD_DISPLACEMENT_LENGTH;
        } else if (b70 == 0xff) {
            throw new BadOpCodeException();
            //return INSTRUCTION_LENGTH + LONG_DISPLACEMENT_LENGTH;
        } else {
            boolean met = conditionMet(ctx, b118);
            if (met) {
                ctx.addPc(2 + Bytes.extendSign((short) b70));
            } else {
                ctx.addPc(INSTRUCTION_LENGTH);
            }
            log.info("    B{} ({}[d8], PC){}", conditionM[b118], b70, met ? "+" : "-");
            return INSTRUCTION_LENGTH;
        }
    }

    private static int moveq(Context ctx) {
        int pc = ctx.getPc();
        int instruction = ctx.getMemory().readWord(pc);
        int b119 = getBit119(instruction);
        int b70 = getBit70(instruction);
        int value = extendSign(b70);
        log.info("{} moveq {} -> D{}", String.format("%08x", pc), value, b119);

        ctx.getDataRegisters()[b119] = value;
        log.info("    {}", ctx.debugCCR());
        ctx.clrOverflow();
        ctx.clrCarry();
        if (value < 0) {
            ctx.setNegative();
            ctx.clrZero();
        } else if (value == 0) {
            ctx.clrNegative();
            ctx.setZero();
        }
        log.info("    {}", ctx.debugCCR());

        ctx.addPc(INSTRUCTION_LENGTH);
        return INSTRUCTION_LENGTH;
    }

    private static int divu(Context ctx) {
        log.info("divu");
        return INSTRUCTION_LENGTH;
    }

    private static int divs(Context ctx) {
        log.info("divs");
        return INSTRUCTION_LENGTH;
    }

    private static int sbcd(Context ctx) {
        log.info("sbcd");
        return INSTRUCTION_LENGTH;
    }

    private static int or(Context ctx) {
        log.info("or");
        return INSTRUCTION_LENGTH;
    }

    private static int sub(Context ctx) {
        log.info("sub");
        return INSTRUCTION_LENGTH;
    }

    private static int subx(Context ctx) {
        log.info("subx");
        return INSTRUCTION_LENGTH;
    }

    private static int suba(Context ctx) {
        log.info("suba");
        return INSTRUCTION_LENGTH;
    }

    private static int eor(Context ctx) {
        log.info("eor");
        return INSTRUCTION_LENGTH;
    }

    private static int cmpm(Context ctx) {
        log.info("cmpm");
        return INSTRUCTION_LENGTH;
    }

    private static int cmp(Context ctx) {
        log.info("cmp");
        return INSTRUCTION_LENGTH;
    }

    private static int cmpa(Context ctx) {
        log.info("cmpa");
        return INSTRUCTION_LENGTH;
    }

    private static int mulu(Context ctx) {
        log.info("mulu");
        return INSTRUCTION_LENGTH;
    }

    private static int muls(Context ctx) {
        log.info("muls");
        return INSTRUCTION_LENGTH;
    }

    private static int abcd(Context ctx) {
        log.info("abcd");
        return INSTRUCTION_LENGTH;
    }

    private static int exg(Context ctx) {
        log.info("exg");
        return INSTRUCTION_LENGTH;
    }

    private static int and(Context ctx) {
        log.info("and");
        return INSTRUCTION_LENGTH;
    }

    private static int add(Context ctx) {
        log.info("add");
        return INSTRUCTION_LENGTH;
    }

    private static int addx(Context ctx) {
        log.info("addx");
        return INSTRUCTION_LENGTH;
    }

    private static int adda(Context ctx) {
        log.info("adda");
        return INSTRUCTION_LENGTH;
    }

    private static int asd(Context ctx) {
        log.info("asd");
        return INSTRUCTION_LENGTH;
    }

    private static int lsd(Context ctx) {
        log.info("lsd");
        return INSTRUCTION_LENGTH;
    }

    private static int roxd(Context ctx) {
        log.info("roxd");
        return INSTRUCTION_LENGTH;
    }

    private static int rod(Context ctx) {
        log.info("rod");
        return INSTRUCTION_LENGTH;
    }

    public static int innerRun(Context ctx) {
        int b1512 = getBits1512(ctx.getMemory().readWord((ctx.getPc())));
        switch (b1512) {
            case 0:
                return handle0000(ctx);
            case 1:
            case 2:
            case 3:
                return handle00xx(ctx);
            //return handle0001(ctx);
            //return handle0010(ctx);
            //return handle0011(ctx);
            case 4:
                return handle0100(ctx);
            case 5:
                return handle0101(ctx);
            case 6:
                return handle0110(ctx);
            case 7:
                return handle0111(ctx);
            case 8:
                return handle1000(ctx);
            case 9:
                return handle1001(ctx);
            //case 10:
            //    return handle1010(ctx);
            case 11:
                return handle1011(ctx);
            case 12:
                return handle1100(ctx);
            case 13:
                return handle1101(ctx);
            case 14:
                return handle1110(ctx);
            //case 15:
            //    return handle1111(ctx);
        }
        throw new RuntimeException("bad op code");
    }

    public static void run(Context ctx) {
        innerRun(ctx);
    }

}
