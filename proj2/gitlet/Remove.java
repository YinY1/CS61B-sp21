package gitlet;

import java.io.File;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

/**
 * Represents a gitlet checkout object
 *
 * @author Edward Tsang
 */
public class Remove {
    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so
     */
    public static boolean remove(File file, String name) {
        boolean flag = false;
        // unstage
        File added = join(ADDITION_DIR, name);
        if (added.exists()) {
            Add.deleteOldFiles(added);
            flag = true;
        }
        // delete file in CWD
        Commit h = Methods.readHEAD();
        if (h.isTracked(file)) {
            Methods.writeFile(file, REMOVAL_DIR, name);
            file.delete();
            flag = true;
        }
        return flag;
    }
}
