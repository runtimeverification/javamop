// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class for common functionality
 */
public class Utility {
    /**
     * Delete a directory and all its contents. Every file has to be individually deleted, since
     * there is no built-in java function to delete an entire directory and its contents
     * recursively.
     *
     * @param path The path of the directory to delete.
     * @throws java.io.IOException If it cannot traverse the directories or the files cannot be deleted.
     */
    public static void deleteDirectory(final Path path) throws IOException {
        // http://stackoverflow.com/a/8685959
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }
}
