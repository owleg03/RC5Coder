package org.sivakoleh.rc5.logic.rc5variations;

public interface IRC5Coder {
    void createSubkeys(byte[] key);
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);
}
