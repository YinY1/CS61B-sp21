package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.TEMP_BLOBS_DIR;
import static gitlet.Utils.*;

/**
 * Represents a blob object of gitlet commit object
 *
 * @author Edward Tsang
 */
public class Blob implements Serializable {
    final String content;
    final File file;

    public Blob(File f) {
        this.content = readContentsAsString(f);
        this.file = f;
    }

    /**
     * Reads blobs from DIR to `Repository.blobs`
     */
    public static void readBlobsToRepo(File dir) {
        List<String> names = plainFilenamesIn(dir);
        Repository.blobs = new HashMap<>();
        if (names != null) {
            for (String name : names) {
                File blob = join(dir, name);
                Blob b = readObject(blob, Blob.class);
                Repository.blobs.put(b.file.getAbsolutePath(), blob.getAbsolutePath());
            }
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
        return sha1(readContentsAsString(f) + f.getName());
    }
}
