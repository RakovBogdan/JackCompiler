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

    public static List<String> readFileLines(Path filePath) {
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String collectFileIntoStringWithoutComments(Path filePath) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            int charCode = br.read();
            while (charCode != -1) {
                if (charCode == '/') {
                    int charCoide = br.read();
                    if (charCode == '/') {
                        while (charCode != '\\' && br.read() != 'n') {
                            charCode = br.read();
                        }
                    }
                    if (charCode == '*') {
                        charCode = br.read();
                        while (charCode != '*' && br.read() != '/') {
                            charCode = br.read();
                        }
                    }
                }
                result.append((char) charCode);
            }

            return result.toString();
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
