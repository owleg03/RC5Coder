package org.sivakoleh.rc5.logic;

import org.sivakoleh.rc5.logic.words.IRC5DWord;
import org.sivakoleh.rc5.logic.words.RC5DWordFactory;

import java.nio.ByteBuffer;

public class RC5 {
    private static final int BITS_IN_BYTE_COUNT = 8;
    private static final int DEFAULT_WORD_SIZE = 32;
    private static final int DEFAULT_ROUNDS_COUNT = 12;
    private static final int DEFAULT_KEY_SIZE = 16;

    private int wordSize;
    private int roundsCount;
    private int keySize;
    private byte[] key;
    private int[] S;
    private RC5DWordFactory rc5DWordFactory;

    public RC5(int wordSize, int roundsCount, int keySize, byte[] key) {
        this.wordSize = wordSize;
        this.roundsCount = roundsCount;
        this.keySize = keySize;
        this.key = key;
        rc5DWordFactory = new RC5DWordFactory(wordSize);

        createSubkeys();
    }

    public RC5(byte[] key) {
        this(DEFAULT_WORD_SIZE, DEFAULT_ROUNDS_COUNT, DEFAULT_KEY_SIZE, key);
    }

    private void createSubkeys() {
        // Transform key to L array
        int u = wordSize / BITS_IN_BYTE_COUNT;
        int c = (int) Math.ceil(keySize / u);
        int[] L = new int[c];

        ByteBuffer buffer = ByteBuffer.wrap(key);
        for (int i = 0; i < c; ++i) {
            L[i] = buffer.getShort();
        }

        // Initialize S array
        S = new int[2 * roundsCount + 2];
        S[0] = 0xB7E15163;

        for (int i = 1; i < S.length; ++i) {
            S[i] = S[i - 1] + 0x9E3779B9;
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
            i = (i + 1) % (2 * roundsCount + 2);
            L[j] = rotateLeft((L[j] + A + B), A + B);
            B = L[j];
            j = (j + 1) % c;
        }
    }

    public byte[] encrypt(byte[] text) {
        IRC5DWord dWord = rc5DWordFactory.createDWord(text, S, roundsCount);
        return dWord.performEncryptionRounds();
    }

    public byte[] decrypt(byte[] cypher) {
        IRC5DWord dWord = rc5DWordFactory.createDWord(cypher, S, roundsCount);
        return dWord.performDecryptionRounds();
    }

    private int rotateLeft(int value, int shift) {
        return (value << shift) | (value >>> (wordSize - shift));
    }

    private int rotateRight(int value, int shift) {
        return (value >>> shift) | (value << (wordSize - shift));
    }
}