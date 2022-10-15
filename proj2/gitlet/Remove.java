package gitlet;

import java.io.File;

import static gitlet.Repository.ADDITION_DIR;
import static gitlet.Repository.REMOVAL_DIR;
import static gitlet.Utils.*;

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
        if (Methods.git().isTracked(file, h)) {
            File blob = new File(h.getBlobs().remove(file.getAbsolutePath()));
            Blob b = readObject(blob, Blob.class);
            File to = join(REMOVAL_DIR, b.file.getName());
            writeContents(to, b.content);
            file.delete();
            flag = true;
        }
        return flag;
    }
}
