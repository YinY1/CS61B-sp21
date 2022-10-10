package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents a gitlet checkout object
 *
 * @author Edward Tsang
 */
public class Checkout {
    /**
     * Takes the version of the file as it exists in the head commit
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    public static void checkoutFile(File file) {
        checkoutFile(Methods.readHEAD(), file);
    }

    /**
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    public static void checkoutFile(Commit commit, File file) {
        File checkFrom = commit.getBlobs().get(file);
        if (checkFrom == null) {
            Methods.Exit("File does not exist in that commit.");
        }/*
        // delete current file in ADD_DIR, if added
        File added = join(Repository.ADDITION_DIR, fileName);
        if (added.exists()) {
            Add.deleteOldFiles(added);
        }*/
        //rewrite old file
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.content);
    }
}
