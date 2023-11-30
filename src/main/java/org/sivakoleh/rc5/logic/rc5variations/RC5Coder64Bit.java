package org.sivakoleh.rc5.logic.rc5variations;

import java.nio.ByteBuffer;

// Utilizes long data type to store data in 64-bit format
public class RC5Coder64Bit implements IRC5Coder {

    private static final int WORD_SIZE = 64; // w
    private static final int DEFAULT_ROUNDS_COUNT = 12; // r

    private final byte[] key;
    private final int roundsCount;
    private long[] S;

    public RC5Coder64Bit(byte[] key, int roundsCount) {
        this.key = key;
        this.roundsCount = roundsCount;

        createSubkeys();
    }

    public RC5Coder64Bit(byte[] key) {
        this(key, DEFAULT_ROUNDS_COUNT);
    }

    private void createSubkeys() {
        // Transform key to L array
        int c = Math.max(1, key.length / (WORD_SIZE / 2));
        long[] L = new long[c];

        ByteBuffer buffer = ByteBuffer.wrap(key);
        for (int i = 0; i < c; ++i) {
            L[i] = buffer.getLong();
        }

        // Initialize S array
        S = new long[2 * roundsCount + 2];
        S[0] = 0xB7E15163L;

        for (int i = 1; i < S.length; ++i) {
            S[i] = (S[i - 1] + 0x9E3779B9L);
        }

        // Mix the S array
        int i = 0;
        int j = 0;
        long A = 0;
        long B = 0;
        int t = Math.max(c, 2 * roundsCount + 2);

        for (int s = 0; s < 3 * t; ++s) {
            S[i] = rotateLeft((S[i] + A + B), 3);
            A = S[i];
            i = ((i + 1) % (2 * (roundsCount + 1)));
            L[j] = rotateLeft((L[j] + A + B), (A + B));
            B = L[j];
            j = (j + 1) % c;
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        long[] data = readData(bytes);

        long A = data[0];
        long B = data[1];
        A = A + S[0];
        B = B + S[1];

        for (int i = 1; i <= roundsCount; ++i) {
            A = rotateLeft((A ^ B), B);
            A = (A + S[2 * i]);
            B = rotateLeft((B ^ A), A);
            B = (B + S[2 * i + 1]);
        }

        ByteBuffer resultBuffer = ByteBuffer.allocate(16);
        resultBuffer.putLong(A);
        resultBuffer.putLong(B);
        return resultBuffer.array();
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        long[] data = readData(bytes);

        long A = data[0];
        long B = data[1];

        for (int i = roundsCount; i > 0; --i) {
            B = rotateRight((B - S[2 * i + 1]), A);
            B = (B ^ A);
            A = rotateRight((A - S[2 * i]), B);
            A = (A ^ B);
        }

        B = B - S[1];
        A = A - S[0];

        ByteBuffer resultBuffer = ByteBuffer.allocate(16);
        resultBuffer.putLong(A);
        resultBuffer.putLong(B);
        return resultBuffer.array();
    }

    private long[] readData(byte[] bytes) {
        long[] data = new long[bytes.length / 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < data.length; ++i) {
            data[i] = buffer.getLong();
        }

        return data;
    }

    private long rotateLeft(long value, long shift) {
        return (value << shift) | (value >>> (WORD_SIZE - shift));
    }

    private long rotateRight(long value, long shift) {
        return (value >>> shift) | (value << (WORD_SIZE - shift));
    }
}
