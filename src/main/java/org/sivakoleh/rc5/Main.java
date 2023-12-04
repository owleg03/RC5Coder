package org.sivakoleh.rc5;

import org.sivakoleh.rc5.IO.FileHelper;
import org.sivakoleh.rc5.UI.MainWindow;
import org.sivakoleh.rc5.logic.RC5CoderCBCPadWrapper;
import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;
import org.sivakoleh.rc5.logic.rc5variations.RC5CoderFactory;

import java.security.SecureRandom;

public class Main {

    // Algorithm parameters
    private static final int WORD_SIZE = 16;
    private static final int ROUNDS_COUNT = 16;
    private static final int BLOCK_SIZE = WORD_SIZE * 2 / 8;

    public static void main(String[] args) {
        IRC5Coder rc5Coder = new RC5CoderFactory().createRC5Coder(ROUNDS_COUNT, WORD_SIZE);
        MainWindow mainWindow = new MainWindow(
                new RC5CoderCBCPadWrapper(rc5Coder, BLOCK_SIZE, System.out),
                new FileHelper());
        mainWindow.show();

        consoleRC5Demo();
    }

    private static void consoleRC5Demo() {
        // Data for encryption
        byte[] key = "Birds are cool!!".getBytes();
        byte[] data = "This is a console demo of RC5".getBytes();

        RC5CoderFactory rc5CoderFactory = new RC5CoderFactory();
        IRC5Coder rc5Coder = rc5CoderFactory.createRC5Coder(ROUNDS_COUNT, WORD_SIZE);
        RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper = new RC5CoderCBCPadWrapper(
                rc5Coder,
                BLOCK_SIZE,
                System.out);

        byte[] initializationVector = generateRandomIV();
        byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(data, key, initializationVector);
        byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(dataEncrypted, key);

        System.out.println("\nData: " + new String(data));
        System.out.println("Encrypted data: " + new String(dataEncrypted));
        System.out.println("Decrypted data: " + new String(dataDecrypted));
        System.out.println("\nLOG:");
    }

    private static byte[] generateRandomIV() {
        SecureRandom random = new SecureRandom();
        byte[] initializationVector = new byte[BLOCK_SIZE];
        random.nextBytes(initializationVector);

        return initializationVector;
    }
}