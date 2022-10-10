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
     * Compare the inFile to the file in the parent commit with the same name
     * return ture if they are the same file
     */
    public static boolean compareToOrigin(File inFile, Commit parent) {
        String currentName = Blob.getBlobName(inFile);
        File oldBlob = parent.getBlobs().get(inFile);
        if (oldBlob == null) {
            return false;
        }
        String oldName = oldBlob.getName();
        return !parent.getBlobs().isEmpty() && oldName.equals(currentName);
    }
}
