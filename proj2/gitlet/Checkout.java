package gitlet;

import java.io.File;
import java.util.HashMap;

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
        String oldBlob = commit.getBlobs().get(file.getAbsolutePath());
        if (oldBlob == null) {
            Methods.exit("File does not exist in that commit.");
        }
        //rewrite old file
        assert oldBlob != null;
        File checkFrom = join(Repository.getObjectsDir(oldBlob), Repository.getObjectName(oldBlob));
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

        Repository.clean(Repository.CWD);
        Branch branchToSwitch = Branch.readBranch(name);

        Commit commitToSwitch = Methods.toCommit(branchToSwitch.getHEAD());
        assert commitToSwitch != null;
        HashMap<String, String> old = commitToSwitch.getBlobs();
        for (String oldFile : old.keySet()) {
            String branchName = old.get(oldFile);
            reStoreBlob(join(oldFile), join(Repository.makeObjectDir(branchName)));
        }

        Methods.git().cleanStagingArea();
        Methods.setHEAD(commitToSwitch, branchToSwitch);
    }

    private static void reStoreBlob(File file, File checkFrom) {
        Blob oldBlob = readObject(checkFrom, Blob.class);
        writeContents(file, oldBlob.getContent());
    }
}
