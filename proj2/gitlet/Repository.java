package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Edward Tsang
 */
public class Repository implements Serializable {
    /*
      TODO: add instance variables here.
      List all instance variables of the Repository class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided two examples for you.
     */

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

    /* TODO: fill in the rest of this class. */

    /**
     * creat necessary Dir and file
     * when initialize repository
     */
    public static void initializeRepo() throws IOException {
        File[] Dir = {GITLET_DIR, STAGING_DIR, ADDITION_DIR, TEMP_BLOBS_DIR, REMOVAL_DIR, BLOBS_DIR, COMMITS_DIR, BRANCHES_DIR};
        for (File f : Dir) {
            f.mkdir();
        }
        File f = HEAD;
        if (!f.exists()) {
            f.createNewFile();
        }
    }

    /**
     * set HEAD pointer point to a commit
     */
    public static void setHEAD(Commit commit) {
        writeContents(HEAD, commit.getUid());
    }

    /**
     * return the commit which HEAD points to
     */
    public static Commit readHEAD() {
        String uid = readContentsAsString(HEAD);
        File commit = join(COMMITS_DIR, uid);
        return readObject(commit, Commit.class);
    }

    /**
     * compare the inFile to the file in the parent commit with the same name
     * return ture if they are the same file
     */
    public static boolean compareToOrigin(File inFile, Commit parent) {
        String currentName = Blob.getBlobName(inFile);
        File oldBlob = parent.blobs.get(inFile);
        if (oldBlob == null) {
            return false;
        }
        String oldName = oldBlob.getName();
        return !parent.blobs.isEmpty() && oldName.equals(currentName);
    }

    /**
     * delete all files in Staging Area
     * TODO: recursively
     */
    public static void cleanStagingArea(Commit commit) throws IOException {
        if (commit.getParent() != null) {
            // move blobs to BLOB_DIR
            Blob.moveBlobs(blobs, commit);
            for (File f : blobs.keySet()) {
                String name = f.getName();
                File added = join(ADDITION_DIR, name);
                if (!added.delete()) {
                    Methods.Exit("DeleteError");
                }
            }
            // change blobs DIR in commit
            File newBlobsDIR = join(BLOBS_DIR, commit.getShortUid());
            Blob.readBlobs(newBlobsDIR);
            commit.blobs = blobs;
            blobs = null;
        }
    }

    /**
     * write inFile to destination DIR with a fileName
     */
    public static void writeFile(File inFile, File desDIR, String fileName) throws IOException {
        byte[] outByte = readContents(inFile);
        File out = join(desDIR, fileName);
        out.createNewFile();
        writeContents(out, outByte);
    }
}
