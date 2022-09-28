package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Checkout {
    public static void checkoutFile(File file) throws IOException {
        checkoutFile(readHEAD(), file);
    }

    public static void checkoutFile(Commit commit, File file) throws IOException {
        File checkFrom = commit.blobs.get(file);
        if (checkFrom == null) {
            Methods.Exit("File does not exist in that commit.");
        }
        String fileName = file.getName();
        /*
        // delete current file in ADD_DIR, if added
        File added = join(Repository.ADDITION_DIR, fileName);
        if (added.exists()) {
            Add.deleteOldFiles(added);
        }*/
        //rewrite old file
        String shortCommitName = commit.getShortUid();
        String blobName = checkFrom.getName();
        File blob = join(BLOBS_DIR, shortCommitName, blobName);
        Blob oldBlob = readObject(blob, Blob.class);
        writeFile(oldBlob.file, CWD, fileName);
    }
}
