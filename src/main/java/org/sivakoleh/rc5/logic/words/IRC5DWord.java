package org.sivakoleh.rc5.logic.words;

public interface IRC5DWord {
    byte[] performEncryptionRounds();
    byte[] performDecryptionRounds();
}
