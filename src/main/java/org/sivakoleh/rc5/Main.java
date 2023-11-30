package org.sivakoleh.rc5;

import org.sivakoleh.rc5.IO.FileHelper;
import org.sivakoleh.rc5.UI.MainWindow;
import org.sivakoleh.rc5.logic.RC5CoderCBCPadWrapper;
import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;
import org.sivakoleh.rc5.logic.rc5variations.RC5CoderFactory;

import java.security.SecureRandom;

public class Main {

    // Algorithm parameters
    private static int wordSize = 64;
    private static int roundsCount = 16;
    private static int blockSize = wordSize * 2 / 8;

    public static void main(String[] args) {
//        IRC5Coder rc5Coder = new RC5CoderFactory().createRC5Coder(roundsCount, wordSize);
//        MainWindow mainWindow = new MainWindow(new RC5CoderCBCPadWrapper(rc5Coder, blockSize), new FileHelper());
//        mainWindow.show();

        consoleRC5Demo();
    }

    private static void consoleRC5Demo() {
        // Data for encryption
        byte[] key = "mysecretkey12345".getBytes();
        byte[] data = "mlmo[no i ib ip ibk; m;,bkjbkbjkjbpkjbpkjbkn jhvgchglhk".getBytes();

        RC5CoderFactory rc5CoderFactory = new RC5CoderFactory();
        IRC5Coder rc5Coder = rc5CoderFactory.createRC5Coder(roundsCount, wordSize);
        RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper = new RC5CoderCBCPadWrapper(
                rc5Coder,
                blockSize
        );

        byte[] initializationVector = generateRandomIV(blockSize);
        byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(data, key, initializationVector);
        byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(dataEncrypted, key, initializationVector);

        System.out.println("Data: " + new String(data));
        System.out.println("Encrypted data: " + new String(dataEncrypted));
        System.out.println("Decrypted data: " + new String(dataDecrypted));
    }

    private static byte[] generateRandomIV(int blockSize) {
        SecureRandom random = new SecureRandom();
        byte[] initializationVector = new byte[blockSize];
        random.nextBytes(initializationVector);

        return initializationVector;
    }
}