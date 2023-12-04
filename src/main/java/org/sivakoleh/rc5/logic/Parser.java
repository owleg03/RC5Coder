package org.sivakoleh.rc5.logic;

import java.nio.charset.Charset;

public class Parser {
    private final Charset charset;

    public Parser(Charset charset) {
        this.charset = charset;
    }

    public byte[] parseToByteArray(String string) {
        return string.getBytes(charset);
    }

    public void pasteIntoByteArray(byte[] result, int value, int offset) {
        result[offset] = (byte) value;
        result[offset + 1] = (byte) (value >>> 8);
        result[offset + 2] = (byte) (value >>> 16);
        result[offset + 3] = (byte) (value >>> 24);
    }

    public int parseToInt(byte[] bytes, int offset) {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result |= (bytes[offset + i] & 0xFF) << (8 * i);
        }

        return result;
    }

    public String parseToString(byte[] bytes) {
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            // Append in hexadecimal
            hash.append(String.format("%02X", b));
        }

        return hash.toString();
    }
}
