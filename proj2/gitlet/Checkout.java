package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/**
 * Represents gitlet-checkout and gitlet-reset.
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
        String oldBlob = commit.getBlobs().get(file.getAbsolutePath());
        if (oldBlob == null) {
            Methods.exit("File does not exist in that commit.");
        }
        //rewrite old file
        File checkFrom = join(Repository.makeObjectDir(oldBlob));
        reStoreBlob(file, checkFrom);
    }

    /**
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * Also, at the end of this command,
     * the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch
     * but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch
     */
    public static void checkoutBranch(String name) {
        if (!Branch.isExists(name)) {
            Methods.exit("No such branch exists.");
        }

        Branch currentBranch = Methods.readHEADAsBranch();
        if (currentBranch.getName().equals(name)) {
            Methods.exit("No need to checkout the current branch.");
        }

        Methods.untrackedExist();

        Repository.clean(Repository.CWD);
        Branch branchToSwitch = Branch.readBranch(name);

        Commit commitToSwitch = Methods.toCommit(branchToSwitch.getHEAD());
        HashMap<String, String> old = commitToSwitch.getBlobs();
        for (String oldFile : old.keySet()) {
            String branchName = old.get(oldFile);
            reStoreBlob(join(oldFile), join(Repository.makeObjectDir(branchName)));
        }

        Methods.readStagingArea().cleanStagingArea();
        Methods.setHEAD(commitToSwitch, branchToSwitch);
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     * See the intro for an example of what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared.
     * The command is essentially checkout of an arbitrary commit that
     * also changes the current branch head.
     */
    public static void reset(Commit commit) {
        Repository.clean(Repository.CWD);
        Methods.readStagingArea().cleanStagingArea();
        Map<String, String> olds = commit.getBlobs();
        olds.keySet().forEach(f -> checkoutFile(commit, join(f)));
        Methods.setHEAD(commit, Methods.readHEADAsBranch());
    }

    /**
     * Read file content from blob, then write it to file.
     *
     * @param file      the file to be checkout
     * @param checkFrom the blob which points to the file
     */
    private static void reStoreBlob(File file, File checkFrom) {
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.getContent());
    }
}
