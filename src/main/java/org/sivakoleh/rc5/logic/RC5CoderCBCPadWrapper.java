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

    public int getBlockSize() {
        return blockSize;
    }

    public byte[] encrypt(byte[] data, byte[] key, byte[] initializationVector) {
        logPrintStream.println("Encrypting..");

        // Count blocks (+ 1 for including the embedded IV, + 1 in case of full padding)
        int blocksCount = (int) Math.ceil((double) data.length / blockSize) + 1;
        if (data.length % blockSize == 0) {
            ++blocksCount;
        }
        byte[] encryptionResult = new byte[blocksCount * blockSize];
        byte[] previousBlock = initializationVector.clone();

        rc5Coder.createSubkeys(key);

        // Add the encrypted IV
        byte[] initializationVectorEncrypted = rc5Coder.encrypt(initializationVector);
        System.arraycopy(initializationVectorEncrypted, 0, encryptionResult, 0, blockSize);

        for (int i = 1; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            int blockLength = Math.min(blockSize, data.length - (i - 1) * blockSize);
            blockLength = Math.max(blockLength, 0);
            System.arraycopy(data, (i - 1) * blockSize, block, 0, blockLength);

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

    public byte[] decrypt(byte[] dataEncrypted, byte[] key) {
        logPrintStream.println("Decrypting..");

        if (dataEncrypted.length == 0) {
            logPrintStream.println("Decrypted!");
            return new byte[0];
        }

        // Count data blocks
        int blocksCount = (int) Math.ceil((double) dataEncrypted.length / blockSize);
        int decryptedLength = (blocksCount - 1) * blockSize;
        byte[] decryptionResult = new byte[decryptedLength];

        rc5Coder.createSubkeys(key);

        // Get the decrypted IV
        byte[] initializationVectorEncrypted = new byte[blockSize];
        System.arraycopy(dataEncrypted, 0, initializationVectorEncrypted, 0, blockSize);
        byte[] previousBlock = rc5Coder.decrypt(initializationVectorEncrypted);

        for (int i = 1; i < blocksCount; ++i) {
            byte[] block = new byte[blockSize];
            System.arraycopy(dataEncrypted, i * blockSize, block, 0, blockSize);

            byte[] decryptedBlock = rc5Coder.decrypt(block);

            for (int j = 0; j < blockSize; ++j) {
                decryptedBlock[j] ^= previousBlock[j];
            }

            System.arraycopy(decryptedBlock, 0, decryptionResult, (i - 1) * blockSize, blockSize);
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
