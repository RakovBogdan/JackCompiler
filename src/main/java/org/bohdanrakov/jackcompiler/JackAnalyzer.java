package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.compilationengine.CompilationEngine;
import org.bohdanrakov.jackcompiler.compilationengine.SemanticXMLCompilationEngine;
import org.bohdanrakov.jackcompiler.compilationengine.TokenXMLCompilationEngine;
import org.bohdanrakov.jackcompiler.tokenizer.JackTokenizer;
import org.bohdanrakov.jackcompiler.utils.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JackAnalyzer {

    public static final String ARGUMENT_MISSING = "Resource name wasn't passed as program argument";
    public static final String JackVMExtension = ".vm";

    private static CompilationEngine compilationEngine;

    public static void main(String[] args) {
        compilationEngine = new SemanticXMLCompilationEngine();
        checkResourceArgument(args);
        String resourceToParse = args[0];

        List<Path> filePaths = FileUtil.parseResourceToFilePaths(resourceToParse);
        for (Path filePath : filePaths) {
            String jackProgram = FileUtil.readFile(filePath);
            List<String> result = compileJackProgram(jackProgram);
            Path newFilePath = getNewFilePathWithVMExtension(filePath);
            FileUtil.writeLinesToNewFile(result, newFilePath);
        }
    }

    private static List<String> compileJackProgram(String jackProgram) {
        JackTokenizer jackTokenizer = new JackTokenizer(jackProgram);
        jackTokenizer.tokenize();
        return compilationEngine.compileClass(jackTokenizer.getTokens());
    }

    private static Path getNewFilePathWithVMExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String newFileName = FileUtil.changeExtensionInFileName(fileName, JackVMExtension);
        return filePath.resolveSibling(Paths.get(newFileName));
    }

    private static void checkResourceArgument(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(ARGUMENT_MISSING);
        }
    }
}
