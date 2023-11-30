package org.sivakoleh.rc5.logic.rc5variations;

public class RC5CoderFactory {
    private static final int BIT_COUNT_16 = 16;
    private static final int BIT_COUNT_32 = 32;
    private static final int BIT_COUNT_64 = 64;

    public IRC5Coder createRC5Coder(int roundsCount, int wordSize) {
        return switch (wordSize) {
            case BIT_COUNT_16 -> new RC5Coder16Bit(roundsCount);
            case BIT_COUNT_32 -> new RC5Coder32Bit(roundsCount);
            case BIT_COUNT_64 -> new RC5Coder64Bit(roundsCount);
            default -> throw new IllegalArgumentException(String.format("Word size of %d is unsupported", wordSize));
        };
    }
}
