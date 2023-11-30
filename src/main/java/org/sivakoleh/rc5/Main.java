package org.sivakoleh.rc5;

import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;
import org.sivakoleh.rc5.logic.rc5variations.RC5CoderFactory;

public class Main {

    public static void main(String[] args) {
        byte[] key = "32bitsAtLeast".getBytes();
        byte[] plaintext = "Hello, RC5!1234Hello, RC5!1234".getBytes();

        RC5CoderFactory rc5CoderFactory = new RC5CoderFactory();
        IRC5Coder rc5Coder = rc5CoderFactory.createRC5Coder(key, 12, 64);
        byte[] ciphertext = rc5Coder.encrypt(plaintext);

        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println("Ciphertext: " + new String(ciphertext));

        byte[] decryptedText = rc5Coder.decrypt(ciphertext);
        System.out.println("Decrypted Text: " + new String(decryptedText));
    }
}