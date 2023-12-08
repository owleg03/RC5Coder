package org.sivakoleh.rc5;

import org.sivakoleh.rc5.IO.FileHelper;
import org.sivakoleh.rc5.UI.MainWindow;
import org.sivakoleh.rc5.logic.*;
import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;
import org.sivakoleh.rc5.logic.rc5variations.RC5CoderFactory;

import java.nio.charset.StandardCharsets;

public class Main {

    // RC5 parameters
    private static final int RC5_WORD_SIZE = 16;
    private static final int RC5_ROUNDS_COUNT = 16;
    private static final KeySize KEY_SIZE = KeySize.KEY_SIZE_32B;
    private static final int RC5_BLOCK_SIZE = RC5_WORD_SIZE * 2 / 8;

    // Pseudorandom generator parameters
    private final static long PSEUDORANDOM_MODULO = 268435455; // 2^28 - 1
    private final static int PSEUDORANDOM_MULTIPLIER = 3375; // 15^3
    private final static int PSEUDORANDOM_CONSTANT_GROWTH = 4181;
    private final static int PSEUDORANDOM_STARTING_VALUE = 19;

    public static void main(String[] args) {
        IRC5Coder rc5Coder = new RC5CoderFactory().createRC5Coder(RC5_ROUNDS_COUNT, RC5_WORD_SIZE);
        RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper = new RC5CoderCBCPadWrapper(rc5Coder, RC5_BLOCK_SIZE, System.out);

        Parser parser = new Parser(StandardCharsets.UTF_8);
        MD5Hasher md5Hasher = new MD5Hasher(parser);
        PseudorandomGenerator pseudorandomGenerator = new PseudorandomGenerator(
                PSEUDORANDOM_MODULO,
                PSEUDORANDOM_MULTIPLIER,
                PSEUDORANDOM_CONSTANT_GROWTH,
                PSEUDORANDOM_STARTING_VALUE);
        DataGenerator dataGenerator = new DataGenerator(parser, md5Hasher, pseudorandomGenerator);

        System.out.println("\nCONSOLE DEMO:\n");
        runConsoleDemo(rc5CoderCBCPadWrapper, dataGenerator, parser);
        System.out.println("\nWINDOW LOG:\n");

        MainWindow mainWindow = new MainWindow(
                rc5CoderCBCPadWrapper,
                KEY_SIZE,
                dataGenerator,
                new FileHelper());
        mainWindow.show();
    }

    private static void runConsoleDemo(
            RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper,
            DataGenerator dataGenerator,
            Parser parser) {

        String keyPhrase = "oleh1234oleh4321";
        String data = "This is a simple console demo of RC5";

        byte[] key = dataGenerator.generateKey(keyPhrase, KEY_SIZE);
        byte[] initializationVector = dataGenerator.generateInitializationVector(RC5_BLOCK_SIZE);
        byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(parser.parseToByteArray(data), key, initializationVector);
        byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(dataEncrypted, key);

        System.out.println("Data: " + data);
        System.out.println("Data encrypted: " + new String(dataEncrypted));
        System.out.println("Data decrypted: " + new String(dataDecrypted));
    }
}