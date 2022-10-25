package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Methods.readHEADAsCommit;
import static gitlet.Methods.readStagingArea;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

/**
 * Represents gitlet-status.
 *
 * @author Edward Tsang
 */
public class Status {
    /**
     * Displays what branches currently exist
     * and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     * An example of the exact format it should follow is as follows.
     */
    public static void printStatus() {
        Index idx = Methods.readStagingArea();
        printFilenames("=== Branches ===", getBranchesNames());
        printFilenames("\n=== Staged Files ===", idx.getAddedFilenames());
        printFilenames("\n=== Removed Files ===", idx.getRemovedFilenames());
        printFilenames("\n=== Modifications Not Staged For Commit ===",
                getModifiedButNotStagedFilesNames());
        printFilenames("\n=== Untracked Files ===", getFilesNames("untracked"));
        System.out.println();
    }

    /**
     * Print a message and filenames
     */
    private static void printFilenames(String msg, List<String> names) {
        System.out.println(msg);
        if (names != null) {
            names.forEach(System.out::println);
        }
    }

    /**
     * Print a message and filenames
     */
    private static void printFilenames(String msg, Set<String> names) {
        printFilenames(msg, new ArrayList<>(names));
    }

    /**
     * Get all branches' names.
     * The current HEAD branch has a `*` in front of its name
     * e.g. *master.
     */
    private static List<String> getBranchesNames() {
        List<String> bs = plainFilenamesIn(Repository.BRANCHES_DIR);
        if (bs == null) {
            return null;
        }
        List<String> branches = new ArrayList<>(bs);
        String name = Methods.readHEADAsBranch().toString();
        branches.remove(name);
        branches.add(0, "*" + name);
        return branches;

    }

    /**
     * A file in the working directory is “modified but not staged” if it is
     * <p>
     * Tracked in the current commit, changed in the working directory, but not staged; or
     * <br>Staged for addition, but with different contents than in the working directory; or
     * <br>Staged for addition, but deleted in the working directory; or
     * <br>Not staged for removal,
     * but tracked in the current commit and deleted from the working directory.
     */
    private static Set<String> getModifiedButNotStagedFilesNames() {
        Index judge = readStagingArea();
        Set<String> ret = new HashSet<>();
        Commit h = readHEADAsCommit();
        for (String filePath : h.getBlobs().keySet()) {
            File f = join(filePath);
            String filename = f.getName();
            boolean exists = f.exists();
            boolean staged = judge.isStaged(f);
            boolean removed = judge.isRemoved(f);
            boolean tracked = judge.isTracked(f, h);
            boolean modified = Index.isModified(f, h);
            if (!exists && (staged || (!removed && tracked))) {
                ret.add(filename + " (deleted)");
            } else if (exists && modified && (tracked || staged)) {
                ret.add(filename + " (modified)");
            }

        }
        return ret;
    }

    /**
     * Files present in the working directory but neither staged for addition nor tracked.
     * This includes files that have been staged for removal,
     * but then re-created without Gitlet’s knowledge.
     *
     * @param mode decide which kind of filenames to return. "untracked" or "tracked"
     * @return untracked\tracked filenames, empty set if mode is incorrect.
     */
    public static Set<String> getFilesNames(String mode) {
        Set<String> ret = new HashSet<>();
        Commit currentCommit = Methods.readHEADAsCommit();
        List<String> files = plainFilenamesIn(Repository.CWD);
        if (files == null) {
            return ret;
        }
        Index idx = readStagingArea();
        for (String f : files) {
            File file = join(Repository.CWD, f);
            boolean flag = idx.isTracked(file, currentCommit);
            if (mode.equals("untracked")) {
                flag = !flag;
            } else if (!mode.equals("tracked")) {
                Methods.exit("DEBUG HERE");
            }
            if (flag) {
                ret.add(file.getName());
            }
        }
        return ret;
    }
}
