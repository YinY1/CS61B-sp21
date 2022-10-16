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
    private final String content;
    private final File file;
    private final String uid;

    public Blob(File f) {
        this.content = readContentsAsString(f);
        this.file = f;
        this.uid = getBlobName(f);
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
        File out = Repository.makeObjectDir(this.uid);
        writeObject(out, this);
        return this.uid;
    }

    public String getUid() {
        return uid;
    }

    public String getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }
}
