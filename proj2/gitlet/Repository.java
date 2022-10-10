package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
     * Staged for removal.
     */
    public static final File REMOVAL_DIR = join(STAGING_DIR, "removal");

    /**
     * The blobs' directory.
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "objects");
    public static final File TEMP_BLOBS_DIR = join(ADDITION_DIR, "temp");
    /**
     * The commit directory.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    /**
     * The branch directory.
     */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");

    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static TreeMap<File, File> blobs;

    /**
     * Creates a new Gitlet version-control system in the current directory.
     */
    public static void initializeRepo() {
        File[] Dir = {GITLET_DIR, STAGING_DIR, ADDITION_DIR, TEMP_BLOBS_DIR, REMOVAL_DIR, BLOBS_DIR, COMMITS_DIR, BRANCHES_DIR};
        for (File f : Dir) {
            f.mkdir();
        }
        File f = HEAD;
        if (!f.exists()) {
            writeContents(f, "");
        }
    }

    /**
     * Sets HEAD pointer point to a commit
     */
    public static void setHEAD(Commit commit) {
        writeContents(HEAD, commit.getUid());
    }

    /**
     * @return the commit which HEAD points to
     */
    public static Commit readHEAD() {
        String uid = readContentsAsString(HEAD);
        return Methods.toCommit(uid);
    }

    /**
     * compare the inFile to the file in the parent commit with the same name
     * return ture if they are the same file
     */
    public static boolean compareToOrigin(File inFile, Commit parent) {
        String currentName = Blob.getBlobName(inFile);
        File oldBlob = parent.getBlobs().get(inFile);
        if (oldBlob == null) {
            return false;
        }
        String oldName = oldBlob.getName();
        return !parent.getBlobs().isEmpty() && oldName.equals(currentName);
    }

    /**
     * delete all files in Staging Area
     * TODO: recursively
     */
    public static void cleanStagingArea(Commit commit) {
        if (commit.getParentAsString() != null) {
            // move blobs to BLOB_DIR
            Blob.moveTempBlobs(commit);
            Blob.moveOlderBlobs(commit);
            clean(ADDITION_DIR);
            clean(REMOVAL_DIR);
            // change blobs DIR in commit
            File newBlobsDIR = join(BLOBS_DIR, commit.getShortUid());
            Blob.readBlobsToRepo(newBlobsDIR);
            commit.setBlobs(blobs);
            blobs = null;
        }
    }

    /**
     * Deletes all files in DIR
     */
    private static void clean(File DIR) {
        List<String> files = plainFilenamesIn(DIR);
        for (String name : files) {
            File f = join(DIR, name);
            if (!f.delete()) {
                Methods.Exit("DeleteError"); //DEBUG
            }
        }
    }

    public static List<File> readRemovalFiles() {
        List<File> ret = new ArrayList<>();
        List<String> names = plainFilenamesIn(REMOVAL_DIR);
        for (String n : names) {
            File rm = join(REMOVAL_DIR, n);
            ret.add(rm);
        }
        return ret;
    }

    /**
     * write inFile to destination DIR with a fileName
     */
    public static void writeFile(File inFile, File desDIR, String fileName) {
        byte[] outByte = readContents(inFile);
        File out = join(desDIR, fileName);
        writeContents(out, outByte);
    }
}
