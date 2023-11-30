package org.sivakoleh.rc5.logic.words;

public class RC5DWordFactory {
    private int wordSizeInBits;

    public RC5DWordFactory(int wordSizeInBits) {
        this.wordSizeInBits = wordSizeInBits;
    }

    public IRC5DWord createDWord(byte[] dataInBytes, int[] S, int roundsCount) {
        switch (wordSizeInBits) {
            case 16: return new RC5DWord16Bit(dataInBytes, S, roundsCount);
            case 32: return new RC5DWord32Bit(dataInBytes, S, roundsCount);
            case 64: return new RC5DWord64Bit(dataInBytes, S, roundsCount);
            default:
                throw new IllegalArgumentException(String.format("Word size of %d is unsupported", wordSizeInBits));
        }
    }
}
