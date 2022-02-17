package javamop.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public static String deleteNewLines(String stringWithNewLines) {
        return stringWithNewLines.replaceAll("[\r\n\\s+]", "");
    }

    public static List<String> getFilesInDir(String dir) {
        List<String> filenames = new ArrayList<>();
        File directory = new File(dir);
        directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File directory, String name) {
                boolean retval = false;
                if (name.endsWith(".mop")) {
                    retval = true;
                    filenames.add(dir + name);
                }
                return retval;
            }
        });
        return filenames;
    }
}
