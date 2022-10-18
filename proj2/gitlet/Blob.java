package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

/**
 * Represents a blob object of gitlet commit object
 *
 * @author Edward Tsang
 */
public class Blob implements Serializable {
    /**
     * The content of the file blob points to.
     */
    private final String content;
    /**
     * The SHA-1 id of the blob object.
     */
    private final String uid;

    /**
     * Instantiate a blob object with a File.
     * A blob means a snapshot of tracked file.
     */
    public Blob(File f) {
        this.content = readContentsAsString(f);
        this.uid = getBlobName(f);
    }

    /**
     * create blob id using file content and filename.
     *
     * @return The blob SHA-1 id
     */
    public static String getBlobName(File f) {
        return sha1(readContentsAsString(f) + f.getName());
    }

    /**
     * Writes Blob as Object to TEMP_DIR.
     *
     * @return blob 40-length uid
     */
    public String makeBlob() {
        File out = Repository.makeObjectDir(this.uid);
        writeObject(out, this);
        return this.uid;
    }

    public String getContent() {
        return content;
    }
}
