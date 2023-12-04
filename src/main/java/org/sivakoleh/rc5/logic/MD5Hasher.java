package org.sivakoleh.rc5.logic;

import java.util.Arrays;

public class MD5Hasher {
    byte[] paddedMessage;
    private final Parser parser;
    private int A;
    private int B;
    private int C;
    private int D;

    private static final int[] T = {
        0xD76AA478, 0xE8C7B756, 0x242070DB, 0xC1BDCEEE,
        0xF57C0FAF, 0x4787C62A, 0xA8304613, 0xFD469501,
        0x698098D8, 0x8B44F7AF, 0xFFFF5BB1, 0x895CD7BE,
        0x6B901122, 0xFD987193, 0xA679438E, 0x49B40821,
        0xF61E2562, 0xC040B340, 0x265E5A51, 0xE9B6C7AA,
        0xD62F105D, 0x02441453, 0xD8A1E681, 0xE7D3FBC8,
        0x21E1CDE6, 0xC33707D6, 0xF4D50D87, 0x455A14ED,
        0xA9E3E905, 0xFCEFA3F8, 0x676F02D9, 0x8D2A4C8A,
        0xFFFA3942, 0x8771F681, 0x6D9D6122, 0xFDE5380C,
        0xA4BEEA44, 0x4BDECFA9, 0xF6BB4B60, 0xBEBFBC70,
        0x289B7EC6, 0xEAA127FA, 0xD4EF3085, 0x04881D05,
        0xD9D4D039, 0xE6DB99E5, 0x1FA27CF8, 0xC4AC5665,
        0xF4292244, 0x432AFF97, 0xAB9423A7, 0xFC93A039,
        0x655B59C3, 0x8F0CCC92, 0xFFEFF47D, 0x85845DD1,
        0x6FA87E4F, 0xFE2CE6E0, 0xA3014314, 0x4E0811A1,
        0xF7537E82, 0xBD3AF235, 0x2AD7D2BB, 0xEB86D391
    };

    private static final int[] S = {
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5,  9, 14, 20, 5,  9, 14, 20, 5,  9, 14, 20, 5, 9,  14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    };

    public MD5Hasher(Parser parser) {
        this.parser = parser;
    }

    private void setPaddedMessage(byte[] message) {
        // Allocate bytes for padding
        int messageLength = message.length;
        int bytesToPadCount = 64 - (messageLength % 64);
        if (bytesToPadCount < 8) {
            bytesToPadCount += 64;
        }
        paddedMessage = new byte[messageLength + bytesToPadCount];


        // Copy the message
        System.arraycopy(message, 0, paddedMessage, 0, messageLength);


        // Append the '1' bit to the end of the message
        paddedMessage[messageLength] = (byte) 0b10000000;


        // Set the message length (in bits, little-endian order)
        long messageBitLength = (long) messageLength * 8;
        for (int i = 0; i < 8; ++i) {
            paddedMessage[paddedMessage.length - 8 + i] = (byte) (messageBitLength >>> (i * 8));
        }
    }

    private void initRegistries() {
        A = 0x67452301;
        B = 0xEFCDAB89;
        C = 0x98BADCFE;
        D = 0x10325476;
    }

    private int[] getBlock(int blockIndex) {
        // Get the bytes for the block
        byte[] bytes = Arrays.copyOfRange(paddedMessage, blockIndex, blockIndex + 64);

        // Divide the block into words
        int[] block = new int[16];
        for (int j = 0; j < 16; j++) {
            block[j] = parser.parseToInt(bytes, j * 4);
        }

        return block;
    }

    private void runRoundsOnTheBlock(int[] block) {
        int AA = A;
        int BB = B;
        int CC = C;
        int DD = D;

        for (int i = 0; i < 64; ++i) {
            int functionResult;
            int blockIndex;

            if (i < 16) {
                functionResult = (BB & CC) | ((~BB) & DD);
                blockIndex = i;
            } else if (i < 32) {
                functionResult = (DD & BB) | ((~DD) & CC);
                blockIndex = (5 * i + 1) % 16;
            } else if (i < 48) {
                functionResult = BB ^ CC ^ DD;
                blockIndex = (3 * i + 5) % 16;
            } else {
                functionResult = CC ^ (BB | (~DD));
                blockIndex = (7 * i) % 16;
            }

            int DDD = DD;
            DD = CC;
            CC = BB;
            BB = BB + Integer.rotateLeft(AA + functionResult + T[i] + block[blockIndex], S[i]);
            AA = DDD;
        }

        A += AA;
        B += BB;
        C += CC;
        D += DD;
    }

    public byte[] calculateHash(byte[] message) {
        // Set padded message
        setPaddedMessage(message);

        // Initialise A, B, C, D
        initRegistries();

        // Iterate over blocks
        for (int i = 0; i < paddedMessage.length; i += 64) {
            int[] block = getBlock(i);
            runRoundsOnTheBlock(block);
        }

        // Combine registries into a result hash
        byte[] hash = new byte[16];
        parser.pasteIntoByteArray(hash, A, 0);
        parser.pasteIntoByteArray(hash, B, 4);
        parser.pasteIntoByteArray(hash, C, 8);
        parser.pasteIntoByteArray(hash, D, 12);

        return hash;
    }
}
