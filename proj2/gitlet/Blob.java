package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private final byte[] content;
    File file;

    public Blob(File f) {
        this.content = readContents(f);
        this.file = f;
    }
}
