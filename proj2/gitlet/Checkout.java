package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeContents;

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
        String oldBlob = commit.getBlobs().get(file.getAbsolutePath());
        if (oldBlob == null) {
            Methods.exit("File does not exist in that commit.");
        }
        //rewrite old file
        assert oldBlob != null;
        File checkFrom = new File(oldBlob);
        reStoreBlob(file, checkFrom);
    }

    public static void checkoutBranch(String name) {
        if (!Branch.isExists(name)) {
            Methods.exit("No such branch exists.");
        }

        Branch currentBranch = Methods.readHEADAsBranch();
        if (currentBranch.getName().equals(name)) {
            Methods.exit("No need to checkout the current branch.");
        }
        if (!Status.getFilesNames("untracked").isEmpty()) {
            Methods.exit("There is an untracked file in the way; delete it,"
                    + " or add and commit it first.");
        }

        Commit currentCommit = Methods.readHEADAsCommit();
        Repository.clean(Repository.CWD);
        Branch branchToSwitch = Branch.readBranch(name);

        Commit oldCommit = Methods.toCommit(branchToSwitch.getHEAD());
        assert oldCommit != null;
        HashMap<String, String> old = oldCommit.getBlobs();
        for (String oldFile : old.keySet()) {
            reStoreBlob(new File(oldFile), new File(old.get(oldFile)));
        }

        Repository.cleanStagingArea(currentCommit);
        Methods.setHEAD(oldCommit, branchToSwitch);
    }

    private static void reStoreBlob(File file, File checkFrom) {
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.content);
    }
}
