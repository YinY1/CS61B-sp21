package gitlet;

import java.io.File;
import java.util.ArrayList;
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
    public static void Exit(String message) {
        System.out.println(message);
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
            Exit("A Gitlet version-control system already exists in the current directory.");
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
        String name = args[1];
        File inFile = join(CWD, name);
        if (!inFile.exists()) {
            Exit("File does not exist.");
        }
        Commit parent = readHEADAsCommit();
        Add.add(inFile, name, parent);
    }

    /**
     * Command 'rm + fileName'.
     * to remove file to unstage
     */
    public static void remove(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        String name = args[1];
        File inFile = join(CWD, name);
        if (!Remove.remove(inFile, name)) {
            Exit("No reason to remove the file.");
        }
    }

    /**
     * Command 'commit + message'
     * to make a commit
     */
    public static void commit(String[] args) {
        exitUnlessRepoExists();
        if (args.length < 2) {
            Exit("Please enter a commit message.");
        }
        judgeOperands(1, args);
        String message = args[1];
        String h = Utils.readObject(HEAD, Branch.class).getHEAD();
        Commit commit = new Commit(message, h);
        commit.makeCommit();
    }

    /**
     * Command `checkout -- [file name]`
     * <p>
     * or  `checkout [commit id] -- [file name]`
     * TODO:       [branch name]
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
                Exit("No commit with that id exists.");
            }
            File file = join(CWD, args[3]);
            Checkout.checkoutFile(commit, file);
        } else if (args.length == 2) {
            Checkout.checkoutBranch(args[1]);
        } else {
            Exit("Incorrect operands.");
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

    /**
     * Command 'find + message'
     * Prints out the ids of all commits that have the given commit message,
     * one per line.
     */
    public static void find(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        List<String> UID = Commit.findWithMessage(args[1]);
        if (UID.isEmpty()) {
            Exit("Found no commit with that message.");
        }
        System.out.println(UID);
    }

    public static void branch(String[] args) {
        exitUnlessRepoExists();
        judgeOperands(1, args);
        Branch b = new Branch(args[1], readHEADContent());
        b.updateBranch();
    }

    /**
     * return true if `.gitlet` exists
     */
    public static void exitUnlessRepoExists() {
        File repo = join(CWD, ".gitlet");
        if (!repo.exists()) {
            Exit("Not in an initialized Gitlet directory.");
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
            Exit("Incorrect operands.");
        }
    }

    /**
     * @param name uid of the commit
     * @return the commit which has the uid if exists
     */
    public static Commit toCommit(String name) {
        File c = join(COMMITS_DIR, name);
        if (!c.exists()) {
            return null;
        }
        return readObject(c, Commit.class);
    }

    /**
     * Reads all files in REMOVAL_DIR
     */
    public static List<File> readRemovalFiles() {
        List<File> ret = new ArrayList<>();
        List<String> names = plainFilenamesIn(REMOVAL_DIR);
        for (String n : names) {
            File rm = join(REMOVAL_DIR, n);
            ret.add(rm);
        }
        return ret;
    }

    /**
     * write inFile to destination DIR with a fileName
     */
    public static void writeFile(File inFile, File desDIR, String fileName) {
        byte[] outByte = readContents(inFile);
        File out = join(desDIR, fileName);
        writeContents(out, outByte);
    }

    /**
     * Sets HEAD pointer point to a commit
     */
    public static void setHEAD(Commit commit) {
        Branch h = readHEADAsBranch();
        h.setHEAD(commit.getUid());
        writeObject(HEAD, h);
        h.updateBranch();
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
}
