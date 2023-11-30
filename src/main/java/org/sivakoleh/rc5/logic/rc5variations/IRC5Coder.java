package org.sivakoleh.rc5.logic.rc5variations;

public interface IRC5Coder {
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);
}
