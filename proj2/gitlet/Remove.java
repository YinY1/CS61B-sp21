package gitlet;

import java.io.File;

import static gitlet.Repository.ADDITION_DIR;
import static gitlet.Repository.REMOVAL_DIR;
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
        Commit h = Methods.readHEADAsCommit();
        if (h.isTracked(file)) {
            File blob = new File(h.getBlobs().remove(file.getAbsolutePath()));
            Methods.writeFile(blob, REMOVAL_DIR, blob.getName());
            file.delete();
            // TODO : maybe should update commit blob.
            flag = true;
        }
        return flag;
    }
}
