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

    public static TreeMap<File, String> blobs;

    /* TODO: fill in the rest of this class. */
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

    public static void setHEAD(Commit commit) {
        writeContents(HEAD, commit.getUid());
    }

    public static Commit readHEAD() {
        String uid = readContentsAsString(HEAD);
        File commit = join(COMMITS_DIR, uid);
        return readObject(commit, Commit.class);
    }

    public static boolean compareToOrigin(File inFile, Commit parent) {
        String name = Blob.getBlobName(inFile);
        String getName = parent.blobs.get(inFile);
        // DEBUG:
        System.out.println(inFile);
        System.out.println(name);
        System.out.println(getName); // null
        System.out.println(parent.blobs);


        return !parent.blobs.isEmpty() && getName != null && getName.equals(name);
    }

    public static void add(File inFile, String name, Commit parent) throws IOException {
        File added = join(ADDITION_DIR, name);
        // if exists, delete the old files
        if (added.exists()) {
            String oldBlobName = Blob.getBlobName(added);
            File oldBlob = join(TEMP_BLOBS_DIR, oldBlobName);
            oldBlob.delete();
            added.delete();
        }
        if (compareToOrigin(inFile, parent)) {
            Methods.Exit("the file has no changes, no need to add");
        }
        // copy file to ADD_DIR
        inFile.createNewFile();
        writeFile(inFile, ADDITION_DIR, name);
        // make temp blob
        Blob b = new Blob(inFile);
        Blob.makeBlob(b);
    }

    public static void remove(String name) {
        // TODO
    }

    public static void cleanStagingArea() throws IOException {
        if (blobs != null) {
            // move blobs to BLOB_DIR
            Blob.moveBlobs(blobs);
            for (File f : blobs.keySet()) {
                String name = f.getName();
                File added = join(ADDITION_DIR,name);
                if (!added.delete()) {
                    Methods.Exit("DeleteError");
                }
                System.out.println("delete " + added.getAbsolutePath());
            }
            blobs = null;
        }
    }

    public static void writeFile(File inFile, File desDIR, String fileName) throws IOException {
        byte[] outByte = readContents(inFile);
        File out = join(desDIR, fileName);
        out.createNewFile();
        writeContents(out, outByte);
    }
}
