package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents a blob object of gitlet commit object
 *
 * @author Edward Tsang
 */
public class Blob implements Serializable {
    final byte[] content;
    File file;

    public Blob(File f) {
        this.content = readContents(f);
        this.file = f;
    }

    /**
     * Reads blobs from DIR to `Repository.blobs`
     */
    public static void readBlobsToRepo(File DIR) {
        List<String> names = plainFilenamesIn(DIR);
        Repository.blobs = new TreeMap<>();
        for (String name : names) {
            File blob = join(DIR, name);
            Blob b = readObject(blob, Blob.class);
            Repository.blobs.put(b.file, blob);
        }
    }

    /**
     * Writes Blob as Object to TEMP_DIR.
     */
    public static void makeBlob(Blob b) {
        String name = getBlobName(b.file);
        File out = join(TEMP_BLOBS_DIR, name);
        writeObject(out, b);
    }

    public static String getBlobName(File f) {
        String name = readContentsAsString(f) + f.getName();
        return sha1(name);
    }

    /**
     * Moves Blobs from TEMP_DIR to BLOBS_DIR.
     */
    public static void moveTempBlobs(Commit commit) {
        for (File b : blobs.values()) {
            // first copy them
            String blobName = b.getName();
            File tempBlob = join(TEMP_BLOBS_DIR, blobName);
            String shortCommitName = commit.getShortUid();
            File blob_DIR = join(BLOBS_DIR, shortCommitName);
            blob_DIR.mkdir();
            writeFile(tempBlob, blob_DIR, blobName);
            // then delete them
            tempBlob.delete();
        }
    }

    /**
     * Copies blobs from Parent's BLOB_DIR to current BLOB_DIR
     */
    public static void moveOlderBlobs(Commit commit) {
        Commit p = commit.getParentAsCommit();
        for (File b : p.getBlobs().values()) {
            String blobName = b.getName();
            String shortCommitName = commit.getShortUid();
            File blob_DIR = join(BLOBS_DIR, shortCommitName);
            writeFile(b, blob_DIR, blobName);
        }
    }
}
