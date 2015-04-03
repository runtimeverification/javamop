// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package examples;

import javamop.util.Tool;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

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
        testCommand(expectedFilePrefix, false, true, command);
    }
    
    /**
     * Execute command, tests return code and potentially checks standard and error output against expected content
     * in files if {@code expectedFilePrefix} not null.
     * @param expectedFilePrefix the prefix for the expected files, or null if output is not checked.
     * @param ignoreOrder when comparing contents of two files, whether to ignore the order of lines or not
     * @param mustSucceed if the program's return code must be {@code 0}.
     * @param commands  list of arguments describing the system command to be executed.
     * @throws Exception
     */
    public void testCommand(String expectedFilePrefix, boolean ignoreOrder, boolean mustSucceed,
                            String... commands) throws Exception {
        testCommand("", expectedFilePrefix, ignoreOrder, mustSucceed, commands);
    }

    /**
     * Execute command, tests return code and potentially checks standard and error output against expected content
     * in files if {@code expectedFilePrefix} not null.
     * @param relativePath The path relative to the MOP file directory to run commands from and find expected files in.
     * @param expectedFilePrefix the prefix for the expected files, or null if output is not checked.
     * @param ignoreOrder when comparing contents of two files, whether to ignore the order of lines or not
     * @param mustSucceed if the program's return code must be {@code 0}.
     * @param command  list of arguments describing the system command to be executed.
     * @throws Exception
     */
    public void testCommand(String relativePath, String expectedFilePrefix, boolean ignoreOrder,
                            boolean mustSucceed, String... command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        processBuilder.directory(new File(basePathFile.toString() + File.separator + relativePath));
        processBuilder.environment().put("CLASSPATH", processBuilder.environment().get("CLASSPATH") + File.pathSeparator
                + System.getProperty("java.class.path"));

        String actualOutFile;
        String testsPrefix;
        String actualErrFile;
        String expectedOutFile = null;
        String expectedErrFile = null;
        if (expectedFilePrefix != null) {
            testsPrefix = basePath.toString() + "/" + relativePath + "/" + expectedFilePrefix;
            actualOutFile = testsPrefix + ".actual.out";
            actualErrFile = testsPrefix + ".actual.err";
            expectedOutFile = testsPrefix + ".expected.out";
            expectedErrFile = testsPrefix + ".expected.err";
        } else {
            if (SystemUtils.IS_OS_WINDOWS) {
                actualOutFile = "NUL";
                actualErrFile = "NUL";
            } else {
                actualOutFile = "/dev/null";
                actualErrFile = "/dev/null";
            }

        }
        processBuilder.redirectError(new File(actualErrFile));
        processBuilder.redirectOutput(new File(actualOutFile));
        Process process = processBuilder.start();
        int returnCode = process.waitFor();
        if(mustSucceed) {
            Assert.assertEquals("Expected no error during" + Arrays.toString(command) + ".", 0, returnCode);
        }
        if (expectedFilePrefix != null) {
            if (!ignoreOrder) {
                assertEqualFiles(expectedOutFile, actualOutFile);
                assertEqualFiles(expectedErrFile, actualErrFile);
            }
            else {
                assertEqualUnorderedFiles(expectedOutFile, actualOutFile);
                assertEqualUnorderedFiles(expectedErrFile, actualErrFile);
            }
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
     * Assert two files have the same contents, but lines can have different order.
     * @param expectedFile The path to the file with the expected result.
     * @param actualFile The path to the file with the calculated result.
     */
    public void assertEqualUnorderedFiles(String expectedFile, String actualFile) throws IOException {
        Set<String> expectedText = Tool.convertFileToStringSet(expectedFile);
        Set<String> actualText = Tool.convertFileToStringSet(actualFile);

        Assert.assertEquals(actualFile + " should match " + expectedFile, expectedText, actualText);
    }

    /**
     * Assert two files have the same contents, ignoring the differences in line separators.
     * @param expectedFile The path to the file with the expected result.
     * @param actualFile The path to the file with the calculated result.
     * @throws IOException
     */
    public void assertEqualFilesIgnoringLineSeparators(String expectedFile, String actualFile) throws IOException {
        String expectedText = Tool.convertFileToString(expectedFile).replace("\n", "").replace("\r", "");
        String actualText = Tool.convertFileToString(actualFile).replace("\n", "").replace("\r", "");

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
            if (!Files.exists(toDelete)) {
                if (fail) {
                    throw new IOException(toDelete.toString() + " does not exist!");
                } else {
                    return;
                }
            }

            if (Files.isDirectory(toDelete)) {
                Tool.deleteDirectory(toDelete);
            } else {
                Files.delete(toDelete);
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
