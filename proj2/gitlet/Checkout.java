package gitlet;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

/**
 * Represents a gitlet checkout object
 *
 * @author Edward Tsang
 */
public class Checkout {
    /**
     * Takes the version of the file as it exists in the head commit
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    public static void checkoutFile(File file) {
        checkoutFile(Methods.readHEADAsCommit(), file);
    }

    /**
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    public static void checkoutFile(Commit commit, File file) {
        File checkFrom = commit.getBlobs().get(file);
        if (checkFrom == null) {
            Methods.exit("File does not exist in that commit.");
        }/*
        // delete current file in ADD_DIR, if added
        File added = join(Repository.ADDITION_DIR, fileName);
        if (added.exists()) {
            Add.deleteOldFiles(added);
        }*/
        //rewrite old file
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.content);
    }

    public static void checkoutBranch(String name) {
        if (!Branch.isExists(name)) {
            Methods.exit("No such branch exists.");
        }
        Branch currentBranch = Methods.readHEADAsBranch();
        if (currentBranch.getName().equals(name)) {
            Methods.exit("No need to checkout the current branch.");
        }
        Commit currentCommit = Methods.readHEADAsCommit();
        List<String> cwd = plainFilenamesIn(Repository.CWD);
        for (String f : cwd) {
            File file = join(Repository.CWD, f);
            if (!currentCommit.isTracked(file)) {
                Methods.exit("There is an untracked file in the way; delete it," +
                        " or add and commit it first.");
            }
        }
        Branch branchToSwitch = Branch.readBranch(name);
        Commit oldCommit = Methods.toCommit(branchToSwitch.getHEAD());
        TreeMap<File, File> old = oldCommit.getBlobs();
        for (File oldFile : old.keySet()) {
            Methods.writeFile(old.get(oldFile), Repository.CWD, oldFile.getName());
        }
        Repository.cleanStagingArea(currentCommit);
        Methods.setHEAD(oldCommit, branchToSwitch);
    }
}
