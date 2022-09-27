package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Edward Tsang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String log;

    /**
     * The SHA-1 id of this Commit.
     */
    TreeMap<File, String> blobs;

    /**
     * The parent Commit of this Commit.
     */
    private final String parent;

    /**
     * The timestamp of this Commit.
     */
    private final Date date;

    private String uid;

    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent) {
        this.log = message;
        this.parent = parent;
        if (parent == null) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
    }

    void makeCommit() throws IOException {
        // make staging area (added) to blobs
        Blob.readBlobs(TEMP_BLOBS_DIR);
        this.blobs = Repository.blobs;
        if(blobs.isEmpty()){// TODO: whether rmArea is empty
            Methods.Exit("No changes added to the commit.");
        }
        byte[] uid = serialize(this);
        setUid(sha1(uid));
        File out = join(COMMITS_DIR, this.uid);
        writeObject(out, this);
        cleanStagingArea();
        setHEAD(this);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }
}
