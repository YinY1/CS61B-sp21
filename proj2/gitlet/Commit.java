package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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
     * The parent Commit of this Commit.
     */
    private final String parent;
    /**
     * The timestamp of this Commit.
     */
    private final Date date;
    /**
     * The snapshots of files of this commit.
     * <p>
     * The keys are files in CWD
     * <p>
     * The values are blobs in BLOB_DIR/shortCommitUid
     */
    private TreeMap<File, File> blobs;
    /**
     * The SHA-1 id of this Commit.
     */
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
     * Finds a commit object matched the Uid
     */
    public static Commit findWithUid(String Uid) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commits) {
            if (commit.equals(Uid) || commit.substring(0, 6).equals(Uid)) {
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
        List<String> Uid = new ArrayList<>();
        for (Commit c : commits) {
            if (c.log.equals(message)) {
                Uid.add(c.uid);
            }
        }
        return Uid;
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
            Methods.exit("No changes added to the commit.");
        }
        byte[] uid = serialize(this);
        setUid(sha1(uid));
        File out = join(COMMITS_DIR, this.uid);
        cleanStagingArea(this);
        writeObject(out, this);
        Methods.setHEAD(this, Methods.readHEADAsBranch());
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
        List<File> rm = Methods.readRemovalFiles();
        if (!rm.isEmpty()) {
            flag = true;
        }
        for (File f : rm) {
            blobs.remove(f);
        }
        return flag;
    }

    public boolean isTracked(File file) {
        return this.blobs.get(file) != null;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public void setBlobs(TreeMap<File, File> b) {
        this.blobs = b;
    }
}
