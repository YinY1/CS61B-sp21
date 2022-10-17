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
     * exit(0) before print message
     */
    public static void exit(String message) {
        if (message != null) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /**
     * Command 'init' initialize `.gitlet`
     * to initialize gitlet repository
     */
    public static void init(String[] args) {
        judgeOperands(0, args);
        File repo = join(CWD, ".gitlet");
        if (repo.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        initializeRepo();
        Commit commit = new Commit("initial commit", null);
        commit.makeCommit();
    }

    /**
     * Command 'add + fileName'.
     * to add file to staging for addition
     */
    public static void add(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        File inFile = join(CWD, args[1]);
        if (!inFile.exists()) {
            exit("File does not exist.");
        }
        readStagingArea().add(inFile);
    }

    /**
     * Command 'rm + fileName'.
     * to remove file to unstage
     */
    public static void remove(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        File inFile = join(CWD, args[1]);
        if (!readStagingArea().remove(inFile)) {
            exit("No reason to remove the file.");
        }
    }

    /**
     * Command 'commit + message'
     * to make a commit
     */
    public static void commit(String[] args) {
        exitUnlessRepoExists();
        if (args.length < 2 || args[1].equals("")) {
            exit("Please enter a commit message.");
        }
        judgeOperands(1, args);
        String message = args[1];
        String h = readHEADContent();
        new Commit(message, h).makeCommit();
    }

    /**
     * Command `checkout -- [file name]`
     * <p>
     * or  `checkout [commit id] -- [file name]`
     * <p>
     * or  `checkout [branch name]`
     */
    public static void checkout(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, 3, args);
        if (args.length == 3 && args[1].equals("--")) {
            File file = join(CWD, args[2]);
            Checkout.checkoutFile(file);
        } else if (args.length == 4 && args[2].equals("--")) {
            Commit commit = Commit.findWithUid(args[1]);
            if (commit == null) {
                exit("No commit with that id exists.");
            }
            File file = join(CWD, args[3]);
            Checkout.checkoutFile(commit, file);
        } else if (args.length == 2) {
            Checkout.checkoutBranch(args[1]);
        } else {
            exit("Incorrect operands.");
        }
    }

    /**
     * Command 'log'
     * to print logs of current commit tree
     */
    public static void log(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(0, args);
        Log.log(readHEADAsCommit());
    }

    /**
     * Command 'global-log'
     * to print logs of all commits
     */
    public static void globalLog(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(0, args);
        Log.globalLog();
    }

    public static void status(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(0, args);
        Status.printStatus();
    }

    /**
     * Command 'find + message'
     * Prints out the ids of all commits that have the given commit message,
     * one per line.
     */
    public static void find(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        List<String> uid = Commit.findWithMessage(args[1]);
        if (uid.isEmpty()) {
            exit("Found no commit with that message.");
        }
        uid.forEach(System.out::println);
    }

    public static void branch(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        Branch b = new Branch(args[1], readHEADContent());
        b.updateBranch();
    }

    public static void removeBranch(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        String name = args[1];
        Branch cur = readHEADAsBranch();
        if (name.equals(cur.getName())) {
            exit("Cannot remove the current branch.");
        } else if (!cur.remove(name)) {
            exit("A branch with that name does not exist.");
        }
    }

    public static void reset(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        Commit commit = toCommit(args[1]);
        if (commit == null) {
            exit("No commit with that id exists.");
        }
        untrackedExist();
        Checkout.reset(commit);
    }

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
     * @param uid uid of the commit
     * @return the commit which has the uid if exists
     */
    public static Commit toCommit(String uid) {
        if (uid == null) {
            return null;
        }
        File c = join(getObjectsDir(uid));
        String rest = getObjectName(uid);
        if (uid.length() < 10) {
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
        return c.exists() ? readObject(c, Commit.class) : null;
    }

    /**
     * Sets HEAD pointer point to a commit
     */
    public static void setHEAD(Commit commit, Branch b) {
        b.setHEADContent(commit.getUid());
        writeObject(HEAD, b);
        b.updateBranch();
    }

    public static Branch readHEADAsBranch() {
        return readObject(HEAD, Branch.class);
    }

    /**
     * @return the commit which HEAD points to
     */
    public static Commit readHEADAsCommit() {
        String uid = readHEADContent();
        return Methods.toCommit(uid);
    }

    public static String readHEADContent() {
        Branch h = readHEADAsBranch();
        return h.getHEAD();
    }

    public static void untrackedExist() {
        if (!Status.getFilesNames("untracked").isEmpty()) {
            Methods.exit("There is an untracked file in the way; delete it,"
                    + " or add and commit it first.");
        }
    }

    public static Index readStagingArea() {
        return readObject(INDEX, Index.class);
    }
}
