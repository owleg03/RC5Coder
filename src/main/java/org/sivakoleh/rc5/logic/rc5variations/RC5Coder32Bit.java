package org.sivakoleh.rc5.logic.rc5variations;

import java.nio.ByteBuffer;

// Utilizes int data type to store data in 32-bit format
public class RC5Coder32Bit implements IRC5Coder {

    private static final int WORD_SIZE = 32; // w
    private static final int DEFAULT_ROUNDS_COUNT = 12; // r

    private final byte[] key;
    private final int roundsCount;
    private int[] S;

    public RC5Coder32Bit(byte[] key, int roundsCount) {
        this.key = key;
        this.roundsCount = roundsCount;

        createSubkeys();
    }

    public RC5Coder32Bit(byte[] key) {
        this(key, DEFAULT_ROUNDS_COUNT);
    }

    private void createSubkeys() {
        // Transform key to L array
        int c = Math.max(1, key.length / (WORD_SIZE / 4));
        int[] L = new int[c];

        ByteBuffer buffer = ByteBuffer.wrap(key);
        for (int i = 0; i < c; ++i) {
            L[i] = buffer.getInt();
        }

        // Initialize S array
        S = new int[2 * roundsCount + 2];
        S[0] = 0xB7E15163;

        for (int i = 1; i < S.length; ++i) {
            S[i] = (S[i - 1] + 0x9E3779B9);
        }

        // Mix the S array
        int i = 0;
        int j = 0;
        int A = 0;
        int B = 0;
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
        int[] data = readData(bytes);

        int A = data[0];
        int B = data[1];
        A = A + S[0];
        B = B + S[1];

        for (int i = 1; i <= roundsCount; ++i) {
            A = rotateLeft((A ^ B), B);
            A = (A + S[2 * i]);
            B = rotateLeft((B ^ A), A);
            B = (B + S[2 * i + 1]);
        }

        ByteBuffer resultBuffer = ByteBuffer.allocate(8);
        resultBuffer.putInt(A);
        resultBuffer.putInt(B);
        return resultBuffer.array();
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        int[] data = readData(bytes);

        int A = data[0];
        int B = data[1];

        for (int i = roundsCount; i > 0; --i) {
            B = rotateRight((B - S[2 * i + 1]), A);
            B = (B ^ A);
            A = rotateRight((A - S[2 * i]), B);
            A = (A ^ B);
        }

        B = B - S[1];
        A = A - S[0];

        ByteBuffer resultBuffer = ByteBuffer.allocate(8);
        resultBuffer.putInt(A);
        resultBuffer.putInt(B);
        return resultBuffer.array();
    }

    private int[] readData(byte[] bytes) {
        int[] data = new int[bytes.length / 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < data.length; ++i) {
            data[i] = buffer.getInt();
        }

        return data;
    }

    private int rotateLeft(int value, int shift) {
        return (value << shift) | (value >>> (WORD_SIZE - shift));
    }

    private int rotateRight(int value, int shift) {
        return (value >>> shift) | (value << (WORD_SIZE - shift));
    }
}
