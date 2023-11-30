package org.sivakoleh.rc5.logic;

import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;

public class RC5CoderCBCPadWrapper {
    private final IRC5Coder rc5Coder;
    private final int blockSize;
    private final byte[] initializationVector;

    public RC5CoderCBCPadWrapper(IRC5Coder rc5Coder, int blockSize, byte[] initializationVector) {
        this.rc5Coder = rc5Coder;
        this.blockSize = blockSize;
        this.initializationVector = initializationVector;
    }

    public byte[] encrypt(byte[] bytes) {
        int blocksCount = (int) Math.ceil((double) bytes.length / blockSize);
        byte[] result = new byte[blocksCount * blockSize];
        byte[] previousBlock = initializationVector.clone();

        for (int i = 0; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            int blockLength = Math.min(blockSize, bytes.length - i * blockSize);
            System.arraycopy(bytes, i * blockSize, block, 0, blockLength);
            if (blockLength < blockSize) {
                padBlock(block, blockLength);
            }

            for (int j = 0; j < blockSize; ++j) {
                block[j] ^= previousBlock[j];
            }

            byte[] blockEncrypted = rc5Coder.encrypt(block);

            System.arraycopy(blockEncrypted, 0, result, i * blockSize, blockSize);
            System.arraycopy(blockEncrypted, 0, previousBlock, 0, blockSize);
        }

        return result;
    }

    public byte[] decrypt(byte[] bytes) {
        int blocksCount = (int) Math.ceil((double) bytes.length / blockSize);
        byte[] result = new byte[blocksCount * blockSize];
        byte[] previousBlock = initializationVector.clone();

        for (int i = 0; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            System.arraycopy(bytes, i * blockSize, block, 0, blockSize);

            byte[] decryptedBlock = rc5Coder.decrypt(block);

            for (int j = 0; j < blockSize; ++j) {
                decryptedBlock[j] ^= previousBlock[j];
            }

            System.arraycopy(decryptedBlock, 0, result, i * blockSize, blockSize);
            System.arraycopy(bytes, i * blockSize, previousBlock, 0, blockSize);
        }

        return result;
    }

    private void padBlock(byte[] block, int actualLength) {
        block[actualLength] = (byte) 0x80;
        for (int i = actualLength + 1; i < blockSize; ++i) {
            block[i] = 0x00;
        }
    }
}
