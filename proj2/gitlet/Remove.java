package gitlet;

import java.io.File;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

public class Remove {
    public static boolean remove(File file, String name) {
        boolean flag = false;
        // unstage
        File added = join(ADDITION_DIR, name);
        if (added.exists()) {
            Add.deleteOldFiles(added);
            flag = true;
        }
        // delete file in CWD
        Commit h = readHEAD();
        if (h.isTracked(file)) {
            writeFile(file, REMOVAL_DIR, name);
            file.delete();
            flag = true;
        }
        return flag;
    }
}
