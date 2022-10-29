package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents helper methods
 *
 * @author Edward Tsang
 */
public class Methods {

    /**
     * return true if `.gitlet` exists
     */
    public static void exitUnlessRepoExists() {
        File repo = join(CWD, ".gitlet");
        if (!repo.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * judge whether the number of operands if correct,
     * exit(0) if operands are incorrect.
     */
    public static void judgeOperands(int num, String[] args) {
        judgeOperands(num, num, args);
    }

    /**
     * judge whether the number of operands if correct,
     * exit(0) if operands are incorrect.
     */
    public static void judgeOperands(int min, int max, String[] args) {
        if (args.length < min + 1 || args.length > max + 1) {
            exit("Incorrect operands.");
        }
    }

    /**
     * make a commit with given id(8-length or 40-length)
     *
     * @param uid uid of the commit
     * @return the commit with given uid if exists
     */
    public static Commit toCommit(String uid) {
        File c = getObject(uid);
        if (c == null) {
            return null;
        }
        return c.exists() ? readObject(c, Commit.class) : null;
    }

    /**
     * make a blob with given id(40-length)
     *
     * @param uid uid of the blob
     * @return the blob with given uid if exists
     */
    public static Blob toBlob(String uid) {
        File c = getObject(uid);
        if (c == null) {
            return null;
        }
        return c.exists() ? readObject(c, Blob.class) : null;
    }

    /**
     * @return object filepath
     */
    private static File getObject(String uid) {
        if (uid == null) {
            return null;
        }
        File c = join(getObjectsDir(uid));
        String rest = getObjectName(uid);
        if (uid.length() == 8) {
            List<String> commits = plainFilenamesIn(c);
            if (commits == null) {
                return null;
            }
            for (String commit : commits) {
                if (commit.substring(0, 6).equals(rest)) {
                    c = join(c, commit);
                    break;
                }
            }
        } else {
            c = join(c, rest);
        }
        return c;
    }

    /**
     * Sets HEAD pointer point to a commit
     */
    public static void setHEAD(Commit commit, Branch b) {
        b.setHEADContent(commit.getUid());
        writeObject(HEAD, b);
        b.updateBranch();
    }

    /**
     * @return Current branch pointer.
     */
    public static Branch readHEADAsBranch() {
        return readObject(HEAD, Branch.class);
    }

    /**
     * @return The commit which HEAD points to
     */
    public static Commit readHEADAsCommit() {
        String uid = readHEADContent();
        return Methods.toCommit(uid);
    }

    /**
     * @return The commit id which HEAD points to
     */
    public static String readHEADContent() {
        return readHEADAsBranch().getHEADAsString();
    }

    /**
     * Test if there is any untracked file.
     */
    public static void untrackedExist() {
        if (!Status.getFilesNames("untracked").isEmpty()) {
            exit("There is an untracked file in the way; delete it,"
                    + " or add and commit it first.");
        }
    }

    /**
     * @return The index object.
     */
    public static Index readStagingArea() {
        return readObject(INDEX, Index.class);
    }

    public static Remote readRemotes() {
        return readObject(REMOTES, Remote.class);
    }

    /**
     * exit(0) before print message
     */
    public static void exit(String message) {
        if (message != null) {
            System.out.println(message);
        }
        System.exit(0);
    }

    public static void judgeCommand(String[] args, int num) {
        exitUnlessRepoExists();
        judgeOperands(num, args);
    }

    public static File correctPath(String path) {
        path = path.replace("/", File.separator);
        return join(path);
    }

    public static void copy(File source, File target) {
        writeContents(target, readContents(source));
    }
}
