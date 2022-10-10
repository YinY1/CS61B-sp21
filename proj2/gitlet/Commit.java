package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object
 * including its message, id, timestamp,
 * parent pointer, files pointer
 *
 * @author Edward Tsang
 */
public class Commit implements Serializable {
    /**
     * The message of this Commit.
     */
    private final String log;

    /**
     * The SHA-1 id of this Commit.
     */
    private TreeMap<File, File> blobs;

    /**
     * The parent Commit of this Commit.
     */
    private final String parent;

    /**
     * The timestamp of this Commit.
     */
    private final Date date;

    private String uid;

    public Commit(String message, String parent) {
        this.log = message;
        this.parent = parent;
        if (parent == null) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.blobs = new TreeMap<>();
    }

    /**
     * Writes this commit Object to COMMIT_DIR
     * and reset the HEAD pointer
     */
    void makeCommit() {
        //Loads parent's blobs
        if (this.parent != null) {
            this.blobs = this.getParentAsCommit().blobs;
        }
        boolean flag = stage();
        flag = unStage(flag);
        if (this.parent != null && !flag) {
            Methods.Exit("No changes added to the commit.");
        }
        byte[] uid = serialize(this);
        setUid(sha1(uid));
        File out = join(COMMITS_DIR, this.uid);
        cleanStagingArea(this);
        writeObject(out, this);
        setHEAD(this);
    }

    /**
     * Adds staging area (added) to blobs
     */
    private boolean stage() {
        boolean flag = false;
        Blob.readBlobsToRepo(TEMP_BLOBS_DIR);
        this.blobs.putAll(Repository.blobs);
        if (!Repository.blobs.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    /**
     * delete blobs in commit
     */
    private boolean unStage(boolean flag) {
        List<File> rm = Repository.readRemovalFiles();
        if (!rm.isEmpty()) {
            flag = true;
        }
        for (File f : rm) {
            blobs.remove(f);
        }
        return flag;
    }

    /**
     * Finds a commit object matched the Uid
     */
    public static Commit findWithUid(String Uid) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commits) {
            if (commit.equals(Uid)) {
                return Methods.toCommit(commit);
            }
        }
        return null;
    }

    /**
     * Finds out the ids of all commits that have the given commit message
     *
     * @return A list of commits' UID
     */
    public static List<String> findWithMessage(String message) {
        List<Commit> commits = findAll();
        List<String> UID = new ArrayList<>();
        for (Commit c : commits) {
            if (c.log.equals(message)) {
                UID.add(c.uid);
            }
        }
        return UID;
    }

    /**
     * @return All commits have ever made.
     */
    public static List<Commit> findAll() {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        List<Commit> ret = new ArrayList<>();
        for (String commit : commits) {
            ret.add(Methods.toCommit(commit));
        }
        return ret;
    }

    public boolean isTracked(File file) {
        return this.blobs.get(file) != null;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBlobs(TreeMap<File, File> b) {
        this.blobs = b;
    }

    public String getUid() {
        return this.uid;
    }

    public String getParentAsString() {
        return parent;
    }

    public Commit getParentAsCommit() {
        return Methods.toCommit(this.parent);
    }

    public String getShortUid() {
        return this.uid.substring(0, 6);
    }

    public Date getDate() {
        return date;
    }

    public String getLog() {
        return log;
    }

    public TreeMap<File, File> getBlobs() {
        return blobs;
    }
}
