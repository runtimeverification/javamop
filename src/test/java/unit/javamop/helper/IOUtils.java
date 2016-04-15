package javamop.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by xiaohe on 4/15/16.
 */
public class IOUtils {
    public static String readFile(String path) throws IOException {
        Path inputPath = Paths.get(path);
        return new String(Files.readAllBytes(inputPath));
    }
}
