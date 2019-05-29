package org.bohdanrakov.jackcompiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    public static List<Path> parseResourceToFilePaths(String resourceToParse) {
        if (Files.isDirectory(Paths.get(resourceToParse))) {
            try {
                return Files.walk(Paths.get(resourceToParse))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".jack"))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Collections.singletonList(Paths.get(resourceToParse));
        }
    }

    public static String readFile(Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLinesToNewFile(List<String> lines, Path filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String changeExtensionInFileName(String fileName, String newExtension) {
        String nameWithoutExtension = getFileNameWithoutExtension(fileName);
        return nameWithoutExtension + newExtension;
    }
}
