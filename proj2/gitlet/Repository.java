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
    public static void cleanStagingArea(Commit c) {
        if (c.getParentAsString() != null) {
            // move blobs to BLOB_DIR
            Commit p = c.getParentAsCommit();
            if (p != null) {
                copyAllBlobs(p, join(BLOBS_DIR, p.getShortUid()), BLOBS_DIR);
            }

            copyAllBlobs(c, TEMP_BLOBS_DIR, BLOBS_DIR);

            deleteBlobs(c);

            File newBlobsDIR = join(BLOBS_DIR, c.getShortUid());
            Blob.readBlobsToRepo(newBlobsDIR);
            c.setBlobs(blobs);

            clean(ADDITION_DIR);
            clean(TEMP_BLOBS_DIR);
            clean(REMOVAL_DIR);
        }
    }

    /**
     * Deletes all files in DIR
     */
    public static void clean(File dir) {
        List<String> files = plainFilenamesIn(dir);
        if (files != null) {
            for (String name : files) {
                File f = join(dir, name);
                if (!f.delete()) {
                    Methods.exit("DeleteError");
                }
            }
        }
    }

    public static void copyAllBlobs(Commit c, File sourceDir, File desDir) {
        List<String> blobs = plainFilenamesIn(sourceDir);
        if (blobs != null) {
            for (String b : blobs) {
                copyBlob(c, sourceDir, desDir, b);
            }
        }
    }

    /**
     * copy a blob from sourceDir to desDir
     */
    private static void copyBlob(Commit c, File sourceDir, File desDir, String blobName) {
        File from = join(sourceDir, blobName);
        File to = join(desDir, c.getShortUid());
        to.mkdir();
        to = join(to, blobName);
        Blob blob = readObject(from, Blob.class);
        writeObject(to, blob);
    }

    public static void deleteBlobs(Commit c) {
        List<String> files = plainFilenamesIn(REMOVAL_DIR);
        if (files != null) {
            for (String f : files) {
                File from = join(REMOVAL_DIR, f);
                File to = join(BLOBS_DIR, c.getShortUid(), Blob.getBlobName(from));
                to.delete();
            }
        }
    }
}
