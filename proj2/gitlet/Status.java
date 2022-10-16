package gitlet;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Methods.git;
import static gitlet.Methods.readHEADAsCommit;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Status {

    public static void printStatus() {
        Index idx = Methods.git();
        printFilenames("=== Branches ===", getBranchesNames());

        printFilenames("\n=== Staged Files ===", idx.getAddedFilenames());

        printFilenames("\n=== Removed Files ===", idx.getRemovedFilenames());

        printFilenames("\n=== Modifications Not Staged For Commit ===",
                getModifiedButNotStagedFilesNames());

        printFilenames("\n=== Untracked Files ===", getFilesNames("untracked"));
        System.out.println();
    }

    private static void printFilenames(String msg, List<String> names) {
        System.out.println(msg);
        if (names != null) {
            names.forEach(System.out::println);
        }
    }

    private static void printFilenames(String msg, Set<String> names) {
        printFilenames(msg, new ArrayList<>(names));
    }

    public static List<String> getBranchesNames() {
        List<String> bs = plainFilenamesIn(Repository.BRANCHES_DIR);
        if (bs == null) {
            return null;
        }
        List<String> branches = new ArrayList<>(bs);
        String name = Methods.readHEADAsBranch().getName();
        branches.remove(name);
        branches.add(0, "*" + name);
        return branches;

    }

    public static Set<String> getModifiedButNotStagedFilesNames() {
        Index judge = git();
        Set<String> ret = new HashSet<>();
        Commit h = readHEADAsCommit();
        for (String filePath : h.getBlobs().keySet()) {
            File f = join(filePath);
            String filename = f.getName();

            boolean exists = f.exists();
            boolean staged = judge.isStaged(f);
            boolean removed = judge.isRemoved(f);
            boolean tracked = judge.isTracked(f, h);
            boolean modified = judge.isModified(f, h);

            if (!exists && (staged || (!removed && tracked))) {
                ret.add(filename + " (deleted)");
            } else if (modified && (tracked || staged)) {
                ret.add(filename + " (modified)");
            }

        }
        return ret;
    }

    /**
     * return untracked\tracked filenames
     */
    public static Set<String> getFilesNames(String mode) {
        Commit currentCommit = Methods.readHEADAsCommit();
        List<String> files = plainFilenamesIn(Repository.CWD);
        if (files == null) {
            return null;
        }
        Set<String> ret = new HashSet<>();
        Index idx = git();
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
