package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * <p>
 * .gitlet
 * <br>├── refs/
 * <br>│ ├── commits
 * <br>│ └── heads/
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
    /**
     * The references' directory.
     */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /**
     * The branch directory.
     */
    public static final File BRANCHES_DIR = join(REFS_DIR, "heads");
    /**
     * The commits file contains all commits' id.
     */
    public static final File COMMITS = join(REFS_DIR, "commits");
    public static final File REMOTES = join(REFS_DIR, "remotes");
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
     * Creates a new Gitlet version-control system in the current directory.
     */
    public static void initializeRepo() {
        List<File> dirs = List.of(GITLET_DIR, REFS_DIR, OBJECTS_DIR, BRANCHES_DIR);
        dirs.forEach(File::mkdir);
        Branch h = new Branch("master", "");
        writeObject(HEAD, h);
        h.updateBranch();
        writeObject(INDEX, new Index());
        writeObject(REMOTES, new Remote());
        writeContents(COMMITS, "");
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

    /**
     * Get directory of a commit object or a blob object with its first two id.
     */
    public static File getObjectsDir(String id) {
        return join(OBJECTS_DIR, id.substring(0, 2));
    }

    /**
     * Get filename of a commit object or a blob object with its last thirty-eight id.
     */
    public static String getObjectName(String id) {
        return id.substring(2);
    }

    /**
     * Get filepath of a commit object or a blob object with its id.
     */
    public static File makeObjectDir(String id) {
        File out = getObjectsDir(id);
        out.mkdir();
        return join(out, getObjectName(id));
    }

    public static File getRemoteBranchDir(String name) {
        return join(Methods.readRemotes().getRemote(name), "refs", "heads");
    }
}
