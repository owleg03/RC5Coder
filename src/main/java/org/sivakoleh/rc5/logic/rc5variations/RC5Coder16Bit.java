package org.sivakoleh.rc5.logic.rc5variations;

import java.nio.ByteBuffer;

// Utilizes short data type to store data in 16-bit format
public class RC5Coder16Bit implements IRC5Coder {

    private static final int WORD_SIZE = 16; // w
    private static final int DEFAULT_ROUNDS_COUNT = 16; // r

    private final int roundsCount;
    private short[] S;

    public RC5Coder16Bit(int roundsCount) {
        this.roundsCount = roundsCount;
    }

    public RC5Coder16Bit() {
        this(DEFAULT_ROUNDS_COUNT);
    }

    @Override
    public void createSubkeys(byte[] key) {
        // Transform key to L array
        int c = Math.max(1, key.length / (WORD_SIZE / 8));
        short[] L = new short[c];

        ByteBuffer buffer = ByteBuffer.wrap(key);
        for (int i = 0; i < c; ++i) {
            L[i] = buffer.getShort();
        }

        // Initialize S array
        S = new short[2 * roundsCount + 2];
        S[0] = (short) 0xB7E15163;

        for (int i = 1; i < S.length; ++i) {
            S[i] = (short) (S[i - 1] + 0x9E3779B9);
        }

        // Mix the S array
        int i = 0;
        int j = 0;
        int A = 0;
        int B = 0;
        int t = Math.max(c, 2 * roundsCount + 2);

        for (int s = 0; s < 3 * t; ++s) {
            S[i] = rotateLeft((short) (S[i] + A + B), (short) 3);
            A = S[i];
            i = (short) ((i + 1) % (2 * (roundsCount + 1)));
            L[j] = rotateLeft((short) (L[j] + A + B), (short) (A + B));
            B = L[j];
            j = (short) ((j + 1) % c);
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        short[] data = readData(bytes);

        short A = data[0];
        short B = data[1];
        A = (short) (A + S[0]);
        B = (short) (B + S[1]);

        for (int i = 1; i <= roundsCount; ++i) {
            A = rotateLeft((short) (A ^ B), B);
            A = (short) (A + S[2 * i]);
            B = rotateLeft((short) (B ^ A), A);
            B = (short) (B + S[2 * i + 1]);
        }

        ByteBuffer resultBuffer = ByteBuffer.allocate(4);
        resultBuffer.putShort(A);
        resultBuffer.putShort(B);
        return resultBuffer.array();
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        short[] data = readData(bytes);

        short A = data[0];
        short B = data[1];

        for (int i = roundsCount; i > 0; --i) {
            B = rotateRight((short) (B - S[2 * i + 1]), A);
            B = (short) (B ^ A);
            A = rotateRight((short) (A - S[2 * i]), B);
            A = (short) (A ^ B);
        }

        B = (short) (B - S[1]);
        A = (short) (A - S[0]);

        ByteBuffer resultBuffer = ByteBuffer.allocate(4);
        resultBuffer.putShort(A);
        resultBuffer.putShort(B);
        return resultBuffer.array();
    }

    private short[] readData(byte[] bytes) {
        short[] data = new short[bytes.length / 2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < data.length; ++i) {
            data[i] = buffer.getShort();
        }

        return data;
    }

    private short rotateLeft(short value, short shift) {
        return (short) (((value & 0xFFFF) << (shift & 0xF)) | ((value & 0xFFFF) >>> (WORD_SIZE - (shift & 0xF))));
    }

    private short rotateRight(short value, short shift) {
        return (short) (((value & 0xFFFF) >>> (shift & 0xF)) | ((value & 0xFFFF) << (WORD_SIZE - (shift & 0xF))));
    }
}
