package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * <p>
 * .gitlet
 * <br>├── refs/
 * <br>│ └── heads
 * <br>├── objects/
 * <br>├── HEAD
 * <br>└── index
 *
 * @author Edward Tsang
 */
public class Repository implements Serializable {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The branch directory.
     */
    public static final File BRANCHES_DIR = join(REFS_DIR, "heads");

    /**
     * The objects directory which stored blobs and commits
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /**
     * The HEAD pointer.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    /**
     * The index object which stored refs of
     * added files and removed files
     */
    public static final File INDEX = join(GITLET_DIR, "index");
    /**
     * Temp blobs of current command.
     */
    static HashMap<String, String> blobs;

    /**
     * Creates a new Gitlet version-control system in the current directory.
     */
    public static void initializeRepo() {
        List<File> dirs = List.of(GITLET_DIR, REFS_DIR, OBJECTS_DIR, BRANCHES_DIR);
        dirs.forEach(File::mkdir);
        Branch h = new Branch("master", "");
        writeObject(HEAD, h);
        h.updateBranch();
        writeObject(INDEX, new Index());
    }

    /**
     * Deletes all files in DIR
     */
    public static void clean(File dir) {
        List<String> files = plainFilenamesIn(dir);
        if (files != null) {
            files.forEach(n -> join(dir, n).delete());
        }
    }

    public static File getObjectsDir(String id) {
        return join(OBJECTS_DIR, id.substring(0, 2));
    }

    public static String getObjectName(String id) {
        return id.substring(2);
    }
}
