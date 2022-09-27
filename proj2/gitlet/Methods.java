package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

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
     */
    public static void init() throws IOException {
        File repo = join(CWD, ".gitlet");
        if (repo.exists()) {
            Exit("A Gitlet version-control system already exists in the current directory.");
        }
        Repository.initializeRepo();
        Commit commit = new Commit("initial commit", null);
        commit.makeCommit();
    }

    /**
     * Command 'add + fileName'.
     */
    public static void add(String[] args) throws IOException {
        isRepoExists();
        judgeOperands(1, args);
        String name = args[1];
        Commit parent = readHEAD();
        File inFile = join(CWD, name);
        if (!inFile.exists()) {
            Exit("File does not exist.");
        }
        Repository.add(inFile, name, parent);
    }

    /**
     * Command 'commit + message'
     */
    public static void commit(String[] args) throws IOException {
        isRepoExists();
        if (args.length < 2) {
            Exit("Please enter a commit message.");
        }
        judgeOperands(1, args);
        String message = args[1];
        String h = Utils.readContentsAsString(Repository.HEAD);
        Commit commit = new Commit(message, h);
        commit.makeCommit();
    }

    /**
     * return true if `.gitlet` exists
     */
    public static void isRepoExists() {
        File repo = join(CWD, ".gitlet");
        if (!repo.exists()) {
            Exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * judge whether the number of operands if correct
     */
    public static void judgeOperands(int num, String[] args) {
        if (args.length != num + 1) {
            Exit("Incorrect operands.");
        }
    }
}
