package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

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
    private String log;
    /**
     * The parent Commit of this Commit.
     */
    private String parent;

    private String secondParent;
    /**
     * The timestamp of this Commit.
     */
    private Date date;
    /**
     * The snapshots of files of this commit.
     * <p>
     * The keys are files in CWD with absolute path.
     * <p>
     * The values are blobs in BLOB_DIR/shortCommitUid
     */
    private HashMap<String, String> blobs;
    /**
     * The SHA-1 id of this Commit.
     */
    private String uid;

    /**
     * Instantiate a commit object with a message and parent commit uid.
     * The first commit with a message "initial commit" has no parent.
     */
    public Commit(String message, String parent) {
        instantiateCommit(message, parent, null);
    }

    public Commit(String message, String parent, String secondParent) {
        instantiateCommit(message, parent, secondParent);
    }

    /**
     * Finds a commit object matched the Uid
     */
    public static Commit findWithUid(String id) {
        if (id == null) {
            return null;
        }
        Commit ret = Methods.toCommit(id);
        return ret != null ? ret : Methods.toCommit(id.substring(0, 8));
    }

    /**
     * Finds out the ids of all commits that have the given commit message
     *
     * @return A list of commits' UID
     */
    public static List<String> findWithMessage(String message) {
        Set<Commit> commits = findAll();
        List<String> ids = new ArrayList<>();
        for (Commit c : commits) {
            if (c.log.equals(message)) {
                ids.add(c.uid);
            }
        }
        return ids;
    }

    /**
     * @return All commits have ever made, including orphan.
     */
    public static Set<Commit> findAll() {
        Set<Commit> commits = new HashSet<>();
        String cs = readContentsAsString(Repository.COMMITS);
        while (!cs.isEmpty()) {
            commits.add(Methods.toCommit(cs.substring(0, 40)));
            cs = cs.substring(40);
        }
        return commits;
    }

    private void instantiateCommit(String message, String parent, String secondParent) {
        this.log = message;
        this.parent = parent;
        this.secondParent = secondParent;
        if (parent == null) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.blobs = new HashMap<>();
    }

    /**
     * Writes this commit Object to COMMIT_DIR
     * and reset the HEAD pointer
     */
    public void makeCommit() {
        //Loads parent's blobs
        if (this.parent != null) {
            this.blobs = this.getParentAsCommit().blobs;
        }
        Index idx = Methods.readStagingArea();
        boolean flag = getStage(idx);
        flag = unStage(flag, idx);
        if (this.parent != null && !flag) {
            Methods.exit("No changes added to the commit.");
        }
        setUid();
        File out = Repository.makeObjectDir(this.uid);
        idx.cleanStagingArea();
        writeObject(out, this);
        Methods.setHEAD(this, Methods.readHEADAsBranch());
        String cs = readContentsAsString(Repository.COMMITS);
        cs += this.uid;
        writeContents(Repository.COMMITS, cs);
    }

    /**
     * Adds staging area (added) to blobs
     */
    private boolean getStage(Index i) {
        boolean flag = false;
        Map<String, String> added = i.getAdded();
        if (!added.isEmpty()) {
            flag = true;
            this.blobs.putAll(i.getAdded());
        }
        return flag;
    }

    /**
     * Delete blobs in commit
     */
    private boolean unStage(boolean flag, Index i) {
        Set<String> rm = i.getRemoved();
        if (!rm.isEmpty()) {
            flag = true;
            rm.forEach(f -> {
                blobs.remove(f);
                restrictedDelete(f);
            });
        }
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

    public Date getDate() {
        return date;
    }

    public String getLog() {
        return log;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String getBlob(File f) {
        return blobs.get(f.getAbsolutePath());
    }

    public String getSecondParentAsString() {
        return secondParent;
    }

    public Commit getSecondParentAsCommit() {
        return Methods.toCommit(this.secondParent);
    }

}
