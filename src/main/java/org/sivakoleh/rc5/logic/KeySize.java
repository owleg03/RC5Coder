package org.sivakoleh.rc5.logic;

public enum KeySize {
    KEY_SIZE_8B(8),
    KEY_SIZE_16B(16),
    KEY_SIZE_32B(32);

    private final int sizeInBytes;

    KeySize(int sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public int getSizeInBytes() {
        return sizeInBytes;
    }
}
