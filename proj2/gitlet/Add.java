package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;

public class Add {
    /**
     * add File to Staging Area
     */
    public static void add(File inFile, String name, Commit parent) {
        File added = join(Repository.ADDITION_DIR, name);
        // if exists, delete the old files
        if (added.exists()) {
            deleteOldFiles(added);
        }
        if (Repository.compareToOrigin(inFile, parent)) {
            Methods.Exit("the file has no changes, no need to add");
        }
        // copy file to ADD_DIR
        Repository.writeFile(inFile, Repository.ADDITION_DIR, name);
        // make temp blob
        Blob b = new Blob(inFile);
        Blob.makeBlob(b);
    }

    public static void deleteOldFiles(File added) {
        String oldBlobName = Blob.getBlobName(added);
        File oldBlob = join(Repository.TEMP_BLOBS_DIR, oldBlobName);
        oldBlob.delete();
        added.delete();
    }
}
