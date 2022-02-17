package com.runtimeverification.rvmonitor.examples;

import com.runtimeverification.rvmonitor.util.Tool;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Helper class for testing the output of external commands against the expected output.
 * @author TraianSF
 */
public class TestHelper {

    private final FileSystem fileSystem;
    private final File basePathFile;
    private final Path basePath;

    /**
     * Initializes the {@code basePath} field to the parent directory of the specified file path
     * @param filePath  path to the file which prompted this test, used to establish working dir
     */
    public TestHelper(String filePath)   {
        fileSystem = FileSystems.getDefault();
        this.basePath = fileSystem.getPath(filePath).getParent();
        basePathFile = basePath.toFile();

    }
    
    /**
     * Execute command, tests return code and potentially checks standard and error output against expected content
     * in files if {@code expectedFilePrefix} not null.
     * @param expectedFilePrefix the prefix for the expected files, or null if output is not checked.
     * @param command  list of arguments describing the system command to be executed.
     * @throws Exception
     */
    public void testCommand(String expectedFilePrefix, String... command) throws Exception {
        testCommand(expectedFilePrefix, true, command);
    }

    /**
     * Execute command, tests return code and potentially checks standard and error output against expected content
     * in files if {@code expectedFilePrefix} not null.
     * @param expectedFilePrefix the prefix for the expected files, or null if output is not checked.
     * @param mustSucceed if the program's return code must be {@code 0}.
     * @param command  list of arguments describing the system command to be executed.
     * @throws Exception
     */
    public void testCommand(String expectedFilePrefix, boolean mustSucceed, String... command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        processBuilder.directory(basePathFile);
        String actualOutFile = null;
        String testsPrefix;
        String actualErrFile = null;
        String expectedOutFile = null;
        String expectedErrFile = null;
        if (expectedFilePrefix != null) {
            testsPrefix = basePath.toString() + "/" + expectedFilePrefix;
            actualOutFile = testsPrefix + ".actual.out";
            actualErrFile = testsPrefix + ".actual.err";
            expectedOutFile = testsPrefix + ".expected.out";
            expectedErrFile = testsPrefix + ".expected.err";
        } else {
            actualOutFile = "/dev/null";
            actualErrFile = "/dev/null";
        }
        processBuilder.redirectError(new File(actualErrFile));
        processBuilder.redirectOutput(new File(actualOutFile));
        Process process = processBuilder.start();
        int returnCode = process.waitFor();
        if(mustSucceed) {
            Assert.assertEquals("Expected no error during" + Arrays.toString(command) + ".", 0, returnCode);
        }
        if (expectedFilePrefix != null) {
            assertEqualFiles(expectedOutFile, actualOutFile);
            assertEqualFiles(expectedErrFile, actualErrFile);
        }
    }
    
    /**
     * Assert two files have equal content.
     * @param expectedFile The path to the file with the expected result.
     * @param actualFile The path to the file with the calculated result.
     */
    public void assertEqualFiles(String expectedFile, String actualFile) throws IOException {
        String expectedText = Tool.convertFileToString(expectedFile);
        String actualText = Tool.convertFileToString(actualFile);
        
        Assert.assertEquals(actualFile + " should match " + expectedFile, expectedText, actualText);
    }

    /**
     * Moves files from the current directory to the path pointed to by basePath
     * @param files  files to be relocated
     * @throws IOException
     */

    public void relocateFiles(String... files) throws IOException {
        for (String s : files) {
            Path path = fileSystem.getPath(basePath.toString(), s);
            Files.move(
                    fileSystem.getPath(s),
                    path
            );
        }
    }

    /**
     * Deletes files from the basePath, potentially failing if the files don't exist
     * @param fail  if true, it expects the files to exist and fails the test if they don't
     * @param files relative paths (to basePath) of the files to be deleted
     * @throws IOException
     */
    public void deleteFiles(boolean fail, String... files) throws IOException {
        for (String s : files) {
            Path toDelete = fileSystem.getPath(basePath.toString(), s);
            if (fail) {
                Files.delete(toDelete);
            } else {
                Files.deleteIfExists(toDelete);
            }
        }
    }

    /**
     * Computes the path obtained by adding the relative path specified by {@code path} to the
     * {@code basePath}
     * @param path  path relative to {@code basePath} to be computed
     * @return the path obtained by adding {@code path} to {@code basePath}
     */
    public Path getPath(String path) {
        return fileSystem.getPath(basePath.toString(), path);
    }

}
