package org.sivakoleh.rc5.logic;

import org.sivakoleh.rc5.logic.rc5variations.IRC5Coder;

import java.io.PrintStream;

public class RC5CoderCBCPadWrapper {
    private final IRC5Coder rc5Coder;
    private final int blockSize;
    private final PrintStream logPrintStream;

    public RC5CoderCBCPadWrapper(IRC5Coder rc5Coder, int blockSize, PrintStream logPrintStream) {
        this.rc5Coder = rc5Coder;
        this.blockSize = blockSize;
        this.logPrintStream = logPrintStream;
    }

    public byte[] encrypt(byte[] data, byte[] key, byte[] initializationVector) {
        int blocksCount = (int) Math.ceil((double) data.length / blockSize);
        if (data.length % blockSize == 0) {
            ++blocksCount;
        }
        byte[] encryptionResult = new byte[blocksCount * blockSize];
        byte[] previousBlock = initializationVector.clone();

        logPrintStream.println("Encrypting..");

        rc5Coder.createSubkeys(key);

        for (int i = 0; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            int blockLength = Math.min(blockSize, data.length - i * blockSize);
            blockLength = Math.max(blockLength, 0);
            System.arraycopy(data, i * blockSize, block, 0, blockLength);

            if (blockLength < blockSize) {
                padBlock(block, blockLength);
            }

            for (int j = 0; j < blockSize; ++j) {
                block[j] ^= previousBlock[j];
            }

            byte[] blockEncrypted = rc5Coder.encrypt(block);

            System.arraycopy(blockEncrypted, 0, encryptionResult, i * blockSize, blockSize);
            System.arraycopy(blockEncrypted, 0, previousBlock, 0, blockSize);
        }

        logPrintStream.println("Encrypted!");

        return encryptionResult;
    }

    public byte[] decrypt(byte[] dataEncrypted, byte[] key, byte[] initializationVector) {
        int blocksCount = (int) Math.ceil((double) dataEncrypted.length / blockSize);
        int decryptedLength = blocksCount * blockSize;
        byte[] decryptionResult = new byte[decryptedLength];
        byte[] previousBlock = initializationVector.clone();

        logPrintStream.println("Decrypting..");

        if (dataEncrypted.length == 0) {
            logPrintStream.println("Decrypted!");
            return new byte[0];
        }

        rc5Coder.createSubkeys(key);

        for (int i = 0; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            System.arraycopy(dataEncrypted, i * blockSize, block, 0, blockSize);

            byte[] decryptedBlock = rc5Coder.decrypt(block);

            for (int j = 0; j < blockSize; ++j) {
                decryptedBlock[j] ^= previousBlock[j];
            }

            System.arraycopy(decryptedBlock, 0, decryptionResult, i * blockSize, blockSize);
            System.arraycopy(dataEncrypted, i * blockSize, previousBlock, 0, blockSize);
        }

        // Crop the padding
        int paddingLength = decryptionResult[decryptedLength - 1];
        int dataLength = decryptedLength - paddingLength;
        byte[] data = new byte[dataLength];
        System.arraycopy(decryptionResult, 0, data, 0, dataLength);

        System.out.println("Decrypted!");

        return data;
    }

    private void padBlock(byte[] block, int actualLength) {
        byte paddingLength = (byte) (blockSize - actualLength);
        for (int i = actualLength; i < blockSize; ++i) {
            block[i] = paddingLength;
        }
    }
}
