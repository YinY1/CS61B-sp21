package byow.Core;

import java.io.File;
import java.nio.file.Paths;

public class Utils {
    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    public static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

}
