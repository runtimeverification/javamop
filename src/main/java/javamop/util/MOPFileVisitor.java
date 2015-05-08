package javamop.util;

import javamop.JavaMOPMain;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by hx312 on 25/04/2015.
 */
public class MOPFileVisitor extends SimpleFileVisitor<Path> {
    private Path inputDirPath = null;
    private final Path outputDirPath;

    public MOPFileVisitor(Path outputDirPath) {
        this.outputDirPath = outputDirPath;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
                                   final BasicFileAttributes attributes)
        throws IOException {
        if (this.inputDirPath == null) {
            this.inputDirPath = dir;
        } else {
            Files.createDirectories(this.outputDirPath.resolve
                    (this.inputDirPath.relativize(dir)));
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        String fileName = String.valueOf(file.getFileName());
        if (fileName.endsWith(".mop")) {
            //when the mop file is visited, its corresponding folder
            // must have already been created in the output directory
            // by the above preVisitDirectory method.
            Path outputPath = this.outputDirPath.resolve
                    (this.inputDirPath.relativize(file)).getParent();
            JavaMOPMain.main(new String[]{file.toFile().getAbsolutePath(),
                            "-d", outputPath.toFile().getAbsolutePath()});
        }
        return CONTINUE;
    }

}
