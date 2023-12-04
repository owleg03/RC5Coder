package org.sivakoleh.rc5.logic;

public class PseudorandomGenerator {
    private final long modulo;        // m
    private final int multiplier;     // a
    private final int constantGrowth; // c
    private final int startingValue;  // X0

    public PseudorandomGenerator(long modulo, int multiplier, int constantGrowth, int startingValue) {
        this.modulo = modulo;
        this.multiplier = multiplier;
        this.constantGrowth = constantGrowth;
        this.startingValue = startingValue;
    }

    public int[] generateValues(int quantity) {
        if (quantity <= 0) {
            return new int[0];
        }

        int[] values = new int[quantity];
        int previousValue = startingValue;

        for (int i = 0; i < quantity; ++i) {
            values[i] = generateValue(previousValue);
            previousValue = values[i];
        }

        return values;
    }

    private int generateValue(int previousValue) {
        long value = ((long) multiplier * previousValue + constantGrowth) % modulo;
        return (int)value;
    }
}
