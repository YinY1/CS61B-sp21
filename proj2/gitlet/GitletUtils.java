package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Repository.CWD;
import static gitlet.Repository.initializeRepo;
import static gitlet.Utils.join;

public class GitletUtils {
    /**
     * Command 'init' initialize `.gitlet`
     * to initialize gitlet repository
     */
    public static void init(String[] args) {
        Methods.judgeOperands(0, args);
        File repo = join(CWD, ".gitlet");
        if (repo.exists()) {
            Methods.exit("A Gitlet version-control system already exists in the current directory.");
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
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        File inFile = join(CWD, args[1]);
        if (!inFile.exists()) {
            Methods.exit("File does not exist.");
        }
        Methods.readStagingArea().add(inFile);
    }

    /**
     * Command 'rm + fileName'.
     * to remove file to unstage
     */
    public static void remove(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        File inFile = join(CWD, args[1]);
        if (!Methods.readStagingArea().remove(inFile)) {
            Methods.exit("No reason to remove the file.");
        }
    }

    /**
     * Command 'commit + message'
     * to make a commit
     */
    public static void commit(String[] args) {
        Methods.exitUnlessRepoExists();
        if (args.length < 2 || args[1].equals("")) {
            Methods.exit("Please enter a commit message.");
        }
        Methods.judgeOperands(1, args);
        String message = args[1];
        String h = Methods.readHEADContent();
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
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, 3, args);
        if (args.length == 3 && args[1].equals("--")) {
            File file = join(CWD, args[2]);
            Checkout.checkoutFile(file);
        } else if (args.length == 4 && args[2].equals("--")) {
            Commit commit = Commit.findWithUid(args[1]);
            if (commit == null) {
                Methods.exit("No commit with that id exists.");
            }
            File file = join(CWD, args[3]);
            Checkout.checkoutFile(commit, file);
        } else if (args.length == 2) {
            Checkout.checkoutBranch(args[1]);
        } else {
            Methods.exit("Incorrect operands.");
        }
    }

    /**
     * Command 'log'
     * to print logs of current commit tree
     */
    public static void log(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(0, args);
        Log.log(Methods.readHEADAsCommit());
    }

    /**
     * Command 'global-log'
     * to print logs of all commits
     */
    public static void globalLog(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(0, args);
        Log.globalLog();
    }

    /**
     * Command 'status'
     * to print status of current working directory.
     */
    public static void status(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(0, args);
        Status.printStatus();
    }

    /**
     * Command 'find + message'
     * Prints out the ids of all commits that have the given commit message,
     * one per line.
     */
    public static void find(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        List<String> uid = Commit.findWithMessage(args[1]);
        if (uid.isEmpty()) {
            Methods.exit("Found no commit with that message.");
        }
        uid.forEach(System.out::println);
    }

    /**
     * Command 'branch [branch name]'
     * to create a branch with given name.
     */
    public static void branch(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        Branch b = new Branch(args[1], Methods.readHEADContent());
        b.updateBranch();
    }

    /**
     * Command 'rm-branch [branch name]'
     * to remove the branch with given name.
     */
    public static void removeBranch(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        String name = args[1];
        Branch cur = Methods.readHEADAsBranch();
        if (name.equals(cur.toString())) {
            Methods.exit("Cannot remove the current branch.");
        } else if (!cur.remove(name)) {
            Methods.exit("A branch with that name does not exist.");
        }
    }

    /**
     * Command 'reset [commit id]'
     * to reset status of given commit.
     */
    public static void reset(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        Commit commit = Methods.toCommit(args[1]);
        if (commit == null) {
            Methods.exit("No commit with that id exists.");
        }
        Methods.untrackedExist();
        Checkout.reset(commit);
    }

    /**
     * Command 'merge [branch name]'
     * merge given branch to current branch
     */
    public static void merge(String[] args) {
        Methods.exitUnlessRepoExists();
        Methods.judgeOperands(1, args);
        Branch cur = Methods.readHEADAsBranch();
        Branch b = Branch.readBranch(args[1]);
        if (b == null) {
            Methods.exit("A branch with that name does not exist.");
        }
        if (b.toString().equals(cur.toString())) {
            Methods.exit("Cannot merge a branch with itself.");
        }
        if (!Methods.readStagingArea().isCommitted()) {
            Methods.exit("You have uncommitted changes.");
        }
        Methods.untrackedExist();
        Merge.merge(cur, b);
    }
}
