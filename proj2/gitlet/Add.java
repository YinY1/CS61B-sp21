package gitlet;

import java.io.File;

import static gitlet.Utils.join;

/**
 * Represents a gitlet ada object
 *
 * @author Edward Tsang
 */
public class Add {
    /**
     * Adds a copy of the file as it currently exists to the staging area.
     */
    public static void add(File inFile, String name, Commit parent) {
        // if removed, delete the rm
        File rm = join(Repository.REMOVAL_DIR, name);
        rm.delete();

        // if exists, delete the old files
        File added = join(Repository.ADDITION_DIR, name);
        if (added.exists()) {
            deleteOldFiles(added);
        }
        if (!Methods.isModified(inFile, parent)) {
            Methods.exit(null);
        }
        // copy file to ADD_DIR
        Methods.copyFile(inFile, Repository.ADDITION_DIR, name);
        // make temp blob
        Blob b = new Blob(inFile);
        Blob.makeBlob(b);
    }

    /**
     * delete older version of the file to be added,
     * including its temp blob
     */
    public static void deleteOldFiles(File added) {
        String oldBlobName = Blob.getBlobName(added);
        File oldBlob = join(Repository.TEMP_BLOBS_DIR, oldBlobName);
        oldBlob.delete();
        added.delete();
    }
}
