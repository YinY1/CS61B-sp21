package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<String, String> blobs;
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
        this.blobs = new HashMap<>();
    }

    /**
     * Finds a commit object matched the Uid
     */
    public static Commit findWithUid(String id) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        if (commits != null) {
            for (String commit : commits) {
                if (id != null && commit.contains(id)) {
                    return Methods.toCommit(commit);
                }
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
        List<String> ids = new ArrayList<>();
        for (Commit c : commits) {
            if (c.log.equals(message)) {
                ids.add(c.uid);
            }
        }
        return ids;
    }

    /**
     * @return All commits have ever made.
     */
    public static List<Commit> findAll() {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        List<Commit> ret = new ArrayList<>();
        if (commits != null) {
            commits.forEach(c -> ret.add(Methods.toCommit(c)));
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
        setUid();
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
        rm.forEach(f -> blobs.remove(f.getAbsolutePath()));
        return flag;
    }


    public String getUid() {
        return this.uid;
    }

    public void setUid() {
        this.uid = sha1(this.parent + this.date + this.log);
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

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String removeBlob(String file) {
        return this.blobs.remove(file);
    }

    public void addBlobs(HashMap<String, String> b) {
        this.blobs.putAll(b);
    }
}
