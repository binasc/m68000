package com.geerong.test.m68000;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        File file = new File("/Users/sunm/Desktop/r3.gen");
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            while (true) {
                int bytes = is.read(buffer);
                if (bytes == -1) {
                    break;
                }
                out.write(buffer, 0, bytes);
            }
        }

        byte[] romBytes = out.toByteArray();
        logger.info("read {} bytes rom", romBytes.length);

        Rom rom = new Rom(romBytes);
        int romLength = romBytes.length - 0x200;
        logger.info("rom length: {}", romLength);

        Context ctx = new Context();
        ctx.getMemory().setRom(romBytes, 0x200, romBytes.length - 0x200);
        ctx.setPc(0x0);

        for (int i = 0; i < 175; i++) {
            Instructions.run(ctx);
        }

        logger.info("rom begin {} bytes", rom.getBegin());
        logger.info("rom end {} bytes", rom.getEnd());

    }

}
