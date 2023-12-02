package org.sivakoleh.rc5.IO;

import java.io.*;
import java.nio.file.Files;

public class FileHelper {
    public byte[] readBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] bytes, String filePath) {
        File file = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
