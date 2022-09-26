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
        File[] Dir = {GITLET_DIR, STAGING_DIR, ADDITION_DIR, REMOVAL_DIR, BLOBS_DIR, COMMITS_DIR, BRANCHES_DIR};
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

    public static void add(File inFile, String name) throws IOException {
        if (blobs == null) {
            blobs = new TreeMap<>();
        }
        writeFile(inFile, ADDITION_DIR, name);
        File added = join(ADDITION_DIR, name);
        Blob b = new Blob(added);
        makeBlob(b); // TODO: Whether add twice will replace the blob;
    }

    public static void remove(String name) {
        // TODO
    }

    public static void cleanStagingArea() {
        // TODO: fix bug of not being able to delete files
        if (blobs != null) {
            for (File f : blobs.keySet()) {
                if (!restrictedDelete(f)) {
                    Methods.Exit("DeleteError");
                }
                System.out.println("delete");
            }
            blobs = null;
        }
        System.out.println("Nothing");
    }

    public static void writeFile(File inFile, File desDIR, String fileName) throws IOException {
        byte[] outByte = readContents(inFile);
        File out = join(ADDITION_DIR, fileName);
        out.createNewFile();
        writeContents(out, outByte);
    }

    static void makeBlob(Blob b) {
        byte[] byteB = readContents(b.file);
        String name = sha1(byteB);
        File out = join(BLOBS_DIR, name);
        writeObject(out, Blob.class);
        blobs.put(b.file, name);
    }
}
