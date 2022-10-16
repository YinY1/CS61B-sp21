package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.BRANCHES_DIR;
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
     * The keys are files in CWD with absolute path.
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
        Commit ret = Methods.toCommit(id);
        return ret == null ? null : Methods.toCommit(id.substring(0, 8));
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
     * @return All commits have ever made.
     */
    public static Set<Commit> findAll() {
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        Set<Commit> ret = new HashSet<>();
        if (branches != null) {
            for (String b : branches) {
                Branch branch = Branch.readBranch(b);
                Commit commit = Methods.toCommit(branch.getHEAD());
                while (commit != null) {
                    ret.add(commit);
                    commit = commit.getParentAsCommit();
                }
            }
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
        Index idx = Methods.git();
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
    }

    /**
     * Adds staging area (added) to blobs
     */
    private boolean getStage(Index i) {
        boolean flag = false;
        this.blobs.putAll(i.getAdded());
        if (!this.blobs.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    /**
     * delete blobs in commit
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
