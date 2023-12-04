package org.sivakoleh.rc5.logic;

public class DataGenerator {
    private final Parser parser;
    private final MD5Hasher md5Hasher;
    private final PseudorandomGenerator pseudorandomGenerator;

    public DataGenerator(Parser parser, MD5Hasher md5Hasher, PseudorandomGenerator pseudorandomGenerator) {
        this.parser = parser;
        this.md5Hasher = md5Hasher;
        this.pseudorandomGenerator = pseudorandomGenerator;
    }

    public byte[] generateKey(String keyPhrase, KeySize keySize) {
        byte[] keyPhraseHash0 = md5Hasher.calculateHash(parser.parseToByteArray(keyPhrase));
        int keySizeInBytes = keySize.getSizeInBytes();
        byte[] key = new byte[keySizeInBytes];

        switch (keySize) {
            case KEY_SIZE_8B -> System.arraycopy(keyPhraseHash0, keySizeInBytes, key, 0, keySizeInBytes);
            case KEY_SIZE_16B -> System.arraycopy(keyPhraseHash0, 0, key, 0, keySizeInBytes);
            case KEY_SIZE_32B -> {
                System.arraycopy(keyPhraseHash0, 0, key, 0, keySizeInBytes / 2);
                byte[] keyPhraseHash1 = md5Hasher.calculateHash(keyPhraseHash0);
                System.arraycopy(keyPhraseHash1, 0, key, keySizeInBytes / 2, keySizeInBytes / 2);
            }
            default -> throw new IllegalArgumentException("Unsupported key size!");
        }

        return key;
    }

    public byte[] generateInitializationVector(int size) {
        byte[] initializationVector = new byte[size];

        int[] values = pseudorandomGenerator.generateValues(size);
        for (int i = 0; i < size; ++i) {
            initializationVector[i] = (byte) values[i];
        }

        return initializationVector;
    }
}
