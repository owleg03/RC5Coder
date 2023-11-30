package org.sivakoleh.rc5.logic.words;

import java.nio.ByteBuffer;

// Uses long as a 64-bit word storage unit
public class RC5DWord64Bit implements IRC5DWord {
    private static final int WORD_BIT_COUNT = 32;

    private long A;
    private long B;
    private int[] S;
    private int roundsCount;

    RC5DWord64Bit(byte[] dataInBytes, int[] S, int roundsCount) {
        this.S = S;
        this.roundsCount = roundsCount;

        long[] data = new long[dataInBytes.length / 2];
        ByteBuffer buffer = ByteBuffer.wrap(dataInBytes);
        for (int i = 0; i < data.length; ++i) {
            data[i] = buffer.getLong();
        }

        A = data[0];
        B = data[1];
    }

    @Override
    public byte[] performEncryptionRounds() {
        A += S[0];
        B += S[1];

        for (int i = 1; i <= roundsCount; ++i) {
            A = rotateLeft((A ^ B), B) + S[2 * i];
            B = rotateLeft((B ^ A), A) + S[2 * i + 1];
        }

        ByteBuffer resultBuffer = ByteBuffer.allocate(8);
        resultBuffer.putLong(A);
        resultBuffer.putLong(B);

        return resultBuffer.array();
    }

    @Override
    public byte[] performDecryptionRounds() {
        for (int i = roundsCount; i > 0; --i) {
            B = rotateRight(B - S[2 * i + 1], A) ^ A;
            A = rotateRight(A - S[2 * i], B) ^ B;
        }

        B -= S[1];
        A -= S[0];

        ByteBuffer resultBuffer = ByteBuffer.allocate(8);
        resultBuffer.putLong(A);
        resultBuffer.putLong(B);

        return resultBuffer.array();
    }

    private long rotateLeft(long value, long shift) {
        return (value << shift) | (value >>> (WORD_BIT_COUNT - shift));
    }

    private long rotateRight(long value, long shift) {
        return (value >>> shift) | (value << (WORD_BIT_COUNT - shift));
    }
}
