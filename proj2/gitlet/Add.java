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
        File added = join(Repository.ADDITION_DIR, name);
        // if exists, delete the old files
        if (added.exists()) {
            deleteOldFiles(added);
        }
        if (Blob.compareToOrigin(inFile, parent)) {
            Methods.Exit("the file has no changes, no need to add");
        }
        // copy file to ADD_DIR
        Methods.writeFile(inFile, Repository.ADDITION_DIR, name);
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
