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
 * <br>├── branches
 * <br>│ ├── master
 * <br>│ └── ...
 * <br>├── commits
 * <br>│ ├── initial
 * <br>│ └── ...
 * <br>├── index
 * <br>│ ├── additional
 * <br>│ └── removal
 * <br>├── objects
 * <br>│ ├── commit1
 * <br>│ │ ├── blob1
 * <br>│ │ └── ...
 * <br>│ └── ...
 * <br>└── HEAD
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
     * The staging directory.
     */
    public static final File STAGING_DIR = join(GITLET_DIR, "index");

    /**
     * Staged for addition.
     */
    public static final File ADDITION_DIR = join(STAGING_DIR, "addition");
    /**
     * Staged for temp blobs
     */
    public static final File TEMP_BLOBS_DIR = join(ADDITION_DIR, "temp");
    /**
     * Staged for removal.
     */
    public static final File REMOVAL_DIR = join(STAGING_DIR, "removal");
    /**
     * The blobs' directory.
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "objects");
    /**
     * The commit directory.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /**
     * The branch directory.
     */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    /**
     * The HEAD pointer.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**
     * Temp blobs of current command.
     */
    static HashMap<String, String> blobs;

    /**
     * Creates a new Gitlet version-control system in the current directory.
     */
    public static void initializeRepo() {
        File[] dir = {GITLET_DIR, STAGING_DIR, ADDITION_DIR, TEMP_BLOBS_DIR,
                REMOVAL_DIR, BLOBS_DIR, COMMITS_DIR, BRANCHES_DIR};
        for (File f : dir) {
            f.mkdir();
        }
        File f = HEAD;
        if (!f.exists()) {
            Branch h = new Branch("master", "");
            writeObject(f, h);
            h.updateBranch();
        }
    }


    /**
     * delete all files in Staging Area
     * TODO: recursively
     */
    public static void cleanStagingArea(Commit commit) {
        if (commit.getParentAsString() != null) {
            // move blobs to BLOB_DIR
            File blobDir = join(BLOBS_DIR, commit.getShortUid());
            blobDir.mkdir();
            moveTempBlobs(blobDir);
            moveOlderBlobs(commit, blobDir);
            clean(ADDITION_DIR);
            clean(REMOVAL_DIR);
            // change blobs DIR in commit
            File newBlobsDIR = join(BLOBS_DIR, commit.getShortUid());
            Blob.readBlobsToRepo(newBlobsDIR);
            commit.setBlobs(blobs);
        }
    }

    /**
     * Deletes all files in DIR
     */
    public static void clean(File dir) {
        List<String> files = plainFilenamesIn(dir);
        for (String name : files) {
            File f = join(dir, name);
            if (!f.delete()) {
                Methods.exit("DeleteError");
            }
        }
    }

    /**
     * Moves Blobs from TEMP_DIR to BLOBS_DIR.
     */
    private static void moveTempBlobs(File blobDir) {
        if (blobs != null) {
            for (String blobPath : blobs.values()) {
                // first copy them
                File from = new File(blobPath);
                copyBlobTo(from, blobDir);
                // then delete them
                from.delete();
            }
        }
    }

    private static void copyBlobTo(File from, File destination) {
        Blob ob = readObject(from, Blob.class);
        File to = join(destination, from.getName());
        writeObject(to, ob);
    }

    /**
     * Copies blobs from Parent's BLOB_DIR to current BLOB_DIR
     */
    private static void moveOlderBlobs(Commit commit, File blobDir) {
        Commit p = commit.getParentAsCommit();
        for (String blobPath : p.getBlobs().values()) {
            copyBlobTo(new File(blobPath), blobDir);
        }
    }

    public static boolean isStaged(File inFile) {
        return join(ADDITION_DIR, inFile.getName()).exists();
    }

    public static boolean isRemoved(File inFile) {
        return join(REMOVAL_DIR, inFile.getName()).exists();
    }
}
