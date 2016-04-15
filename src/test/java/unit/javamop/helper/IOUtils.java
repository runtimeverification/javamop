package javamop.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Created by xiaohe on 4/15/16.
 */
public class IOUtils {
    public static String readFile(String path) throws IOException {
        Path inputPath = Paths.get(System.getProperty("user.dir") + File.separator + path);
        return new String(Files.readAllBytes(inputPath));
    }

    public static void write2File(String contents, Path outputPath) {
        if (outputPath != null) {
            try {
                outputPath.toFile().getParentFile().mkdirs();

                Files.write(outputPath, contents.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
