package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Methods.*;
import static gitlet.Repository.CWD;
import static gitlet.Repository.initializeRepo;
import static gitlet.Utils.join;

/**
 * Represents gitlet commands
 *
 * @author Edward Tsang
 */
public class GitletUtils {
    /**
     * Command 'init' initialize `.gitlet`
     * to initialize gitlet repository
     */
    public static void init(String[] args) {
        judgeOperands(args, 0);
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
        judgeCommand(args, 1);
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
        judgeCommand(args, 1);
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
        judgeOperands(args, 1);
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
        judgeCommand(args, 0);
        Log.log(readHEADAsCommit());
    }

    /**
     * Command 'global-log'
     * to print logs of all commits
     */
    public static void globalLog(String[] args) {
        judgeCommand(args, 0);
        Log.globalLog();
    }

    /**
     * Command 'status'
     * to print status of current working directory.
     */
    public static void status(String[] args) {
        judgeCommand(args, 0);
        Status.printStatus();
    }

    /**
     * Command 'find + message'
     * Prints out the ids of all commits that have the given commit message,
     * one per line.
     */
    public static void find(String[] args) {
        judgeCommand(args, 1);
        List<String> uid = Commit.findWithMessage(args[1]);
        if (uid.isEmpty()) {
            exit("Found no commit with that message.");
        }
        uid.forEach(System.out::println);
    }

    /**
     * Command 'branch [branch name]'
     * to create a branch with given name.
     */
    public static void branch(String[] args) {
        judgeCommand(args, 1);
        Branch b = new Branch(args[1], readHEADContent());
        b.updateBranch();
    }

    /**
     * Command 'rm-branch [branch name]'
     * to remove the branch with given name.
     */
    public static void removeBranch(String[] args) {
        judgeCommand(args, 1);
        String name = args[1];
        Branch cur = readHEADAsBranch();
        if (name.equals(cur.toString())) {
            exit("Cannot remove the current branch.");
        } else if (!cur.remove(name)) {
            exit("A branch with that name does not exist.");
        }
    }

    /**
     * Command 'reset [commit id]'
     * to reset status of given commit.
     */
    public static void reset(String[] args) {
        judgeCommand(args, 1);
        Commit commit = toCommit(args[1]);
        if (commit == null) {
            exit("No commit with that id exists.");
        }
        untrackedExist();
        Checkout.reset(commit);
    }

    /**
     * Command 'merge [branch name]'
     * merge given branch to current branch
     */
    public static void merge(String[] args) {
        judgeCommand(args, 1);
        Branch cur = readHEADAsBranch();
        Branch b = Branch.readBranch(args[1]);
        if (b == null) {
            exit("A branch with that name does not exist.");
        }
        if (b.toString().equals(cur.toString())) {
            exit("Cannot merge a branch with itself.");
        }
        if (!readStagingArea().isCommitted()) {
            exit("You have uncommitted changes.");
        }
        untrackedExist();
        Merge.merge(cur, b);
    }

    /**
     * Command 'add-remote [remote name] [name of remote directory]/.gitlet'
     * add a new remote and save the given login information under the given remote name.
     */
    public static void addRemote(String[] args) {
        judgeCommand(args, 2);
        if (!readRemotes().addRemote(args[1], correctPath(args[2]))) {
            exit("A remote with that name already exists.");
        }
    }

    /**
     * Command 'rm-remote [remote name]'
     * remove information associated with the given remote name
     */
    public static void rmRemote(String[] args) {
        judgeCommand(args, 1);
        if (!readRemotes().removeRemote(args[1])) {
            exit("A remote with that name does not exist.");
        }
    }

    /**
     * Command 'push [remote name] [remote branch name]'
     * push local branch to remote
     */
    public static void push(String[] args) {
        judgeCommand(args, 2);
        Remote r = readRemotes();
        String remoteName = args[1];
        String branchName = args[2];
        if (!r.isExists(remoteName) || !r.getRemote(remoteName).exists()) {
            Methods.exit("Remote directory not found.");
        }
        r.push(remoteName, Branch.readBranch(branchName, remoteName));
    }

    /**
     * Command 'fetch [remote name] [remote branch name]'
     * fetch remote branch to local
     */
    public static void fetch(String[] args) {
        judgeCommand(args, 2);
        Remote r = readRemotes();
        String remoteName = args[1];
        String branchName = args[2];
        if (!r.isExists(remoteName) || !r.getRemote(remoteName).exists()) {
            Methods.exit("Remote directory not found.");
        }
        r.fetch(remoteName, Branch.readBranch(branchName, remoteName));
    }

}
