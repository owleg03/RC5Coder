package org.sivakoleh.rc5;

import org.sivakoleh.rc5.logic.RC5CoderCBCPadWrapper;
import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;
import org.sivakoleh.rc5.logic.rc5variations.RC5CoderFactory;

import java.security.SecureRandom;

public class Main {

    public static void main(String[] args) {
        // Algorithm parameters
        int wordSize = 16;
        int roundsCount = 16;

        // Data for encryption
        byte[] key = "one1two2".getBytes();
        byte[] text = "I actually got it working, woah.".getBytes();

        RC5CoderFactory rc5CoderFactory = new RC5CoderFactory();
        IRC5Coder rc5Coder = rc5CoderFactory.createRC5Coder(key, roundsCount, wordSize);
        RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper = new RC5CoderCBCPadWrapper(
                rc5Coder,
                wordSize * 2 / 8,
                generateRandomIV()
        );


        byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(text);
        byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(dataEncrypted);

        System.out.println("Text: " + new String(text));
        System.out.println("Encrypted text: " + new String(dataEncrypted));
        System.out.println("Decrypted Text: " + new String(dataDecrypted));
    }

    private static byte[] generateRandomIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[8];
        random.nextBytes(iv);
        return iv;
    }
}