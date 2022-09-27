package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    final byte[] content;
    File file;

    public Blob(File f) {
        this.content = readContents(f);
        this.file = f;
    }

    public static void readBlobs(File DIR) {
        List<String> names = plainFilenamesIn(DIR);
        Repository.blobs = new TreeMap<>();
        for (String name : names) {
            File blob = join(DIR, name);
            Blob b = readObject(blob, Blob.class);
            Repository.blobs.put(b.file, name);
        }
    }

    public static void makeBlob(Blob b) {
        String name = getBlobName(b.file);
        File out = join(TEMP_BLOBS_DIR, name);
        writeObject(out, b);
    }

    public static String getBlobName(File f) {
        String name = readContentsAsString(f)+f.getName();
        return sha1(name);
    }

    public static void moveBlobs(TreeMap<File, String> blobs) throws IOException {
        //move blobs from temp to BLOB_DIR
        for (String b : blobs.values()) {
            // first copy them
            File tempBlob = join(TEMP_BLOBS_DIR, b);
            writeFile(tempBlob, BLOBS_DIR, b);
            // then delete them
            tempBlob.delete();
        }


    }
}
