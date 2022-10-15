package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Represents a blob object of gitlet commit object
 *
 * @author Edward Tsang
 */
public class Blob implements Serializable {
    final String content;
    final File file;
    private final String uid;

    public Blob(File f) {
        this.content = readContentsAsString(f);
        this.file = f;
        this.uid = getBlobName(f);
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

    public static String getBlobName(File f) {
        return sha1(readContentsAsString(f) + f.getName());
    }

    /**
     * Writes Blob as Object to TEMP_DIR.
     *
     * @return blob 40-length uid
     */
    public String makeBlob() {
        String name = getBlobName(this.file);
        File out = join(getBlobDir(), getBlobName());
        writeObject(out, this);
        return name;
    }

    public File getBlobDir() {
        return join(Repository.OBJECTS_DIR, this.uid.substring(0, 2));
    }

    public String getBlobName() {
        return this.uid.substring(2);
    }
}
