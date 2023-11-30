package org.sivakoleh.rc5;

import org.sivakoleh.rc5.logic.RC5;

public class Main {

    public static void main(String[] args) {
        byte[] key = "mysecretkey12345".getBytes();
        byte[] plaintext = "Hello, RC5!".getBytes();

        RC5 rc5 = new RC5(16, 12, 16, key);
        byte[] ciphertext = rc5.encrypt(plaintext);

        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println("Ciphertext: " + new String(ciphertext));

        byte[] decryptedText = rc5.decrypt(ciphertext);
        System.out.println("Decrypted Text: " + new String(decryptedText));
    }
}