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
            Methods.Exit("File does not exist in that commit.");
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
        if (Branch.isExists(name)) {
            Methods.Exit("No such branch exists.");
        }
        Branch b = Methods.readHEADAsBranch();
        if (b.getName().equals(name)) {
            Methods.Exit("No need to checkout the current branch.");
        }
        Commit c = Methods.readHEADAsCommit();
        List<String> cwd = plainFilenamesIn(Repository.CWD);
        File blob = join(Repository.BLOBS_DIR, c.getShortUid());
        List<String> blobs = plainFilenamesIn(blob);
        if (!cwd.equals(blobs)) {
            Methods.Exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        b = Branch.readBranch(name);
        c = Methods.toCommit(b.getHEAD());
        TreeMap<File, File> old = c.getBlobs();
        Repository.clean(Repository.CWD);
        for (File oldFile : old.keySet()) {
            Methods.writeFile(old.get(oldFile), Repository.CWD, oldFile.getName());
        }
        Repository.cleanStagingArea(c);
    }
}
