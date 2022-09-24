package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Edward Tsang
 */
public class Commit {
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
    private final String message;

    /**
     * The SHA-1 id of this Commit.
     */
    private final String UID;

    /**
     * The parent Commit of this Commit.
     */
    private final String parent;

    /**
     * The timestamp of this Commit.
     */
    private final Date date;

    private final List<String> addition;
    private final List<String> removal;

    /* TODO: fill in the rest of this class. */

    public Commit(String message, String parent) throws IOException {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            this.date = new Date(0);
        } else {
            this.date = new Date();
        }
        this.addition = readAddingFile();
        this.removal = readRemovingFile();
        this.UID = sha1(this);
        makeCommit();
    }

    private static List<String> readAddingFile() {
        File additionDir = Repository.ADDITION_DIR;
        return plainFilenamesIn(additionDir);
    }

    private static List<String> readRemovingFile(){
        File removalDir = Repository.REMOVAL_DIR;
        return plainFilenamesIn(removalDir);
    }

    private void makeCommit() throws IOException {
        File commit = join(Repository.COMMITS_DIR,this.UID);
        commit.createNewFile();
        writeObject(commit,Commit.class);
    }
}
