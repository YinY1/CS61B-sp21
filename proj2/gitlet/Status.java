package gitlet;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Methods.*;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Status {

    public static void printStatus() {
        printFilenames("=== Branches ===", getBranchesNames());

        printFilenames("\n=== Staged Files ===", ADDITION_DIR);

        printFilenames("\n=== Removed Files ===", REMOVAL_DIR);

        printFilenames("\n=== Modifications Not Staged For Commit ===",
                getModifiedButNotStagedFilesNames());

        printFilenames("\n=== Untracked Files ===", getFilesNames("untracked"));
        System.out.println();
    }

    private static void printFilenames(String msg, File dir) {
        List<String> files = plainFilenamesIn(dir);
        printFilenames(msg, files);
    }

    private static void printFilenames(String msg, List<String> names) {
        System.out.println(msg);
        if (names != null) {
            names.forEach(System.out::println);
        }
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

    public static ArrayList<String> getModifiedButNotStagedFilesNames() {
        Index judge = git();
        ArrayList<String> ret = new ArrayList<>();
        Commit h = readHEADAsCommit();
        for (String filePath : h.getBlobs().keySet()) {
            File f = join(filePath);
            String filename = f.getName();
            if (!f.exists() && (judge.isStaged(f) || (!judge.isRemoved(f) && judge.isTracked(f, h)))) {
                ret.add(filename + " (deleted)");
            } else if ((judge.isTracked(f, h) && isModified(f, h) && !judge.isStaged(f))
                    || (judge.isStaged(f) && isModified(f, h))) {
                ret.add(filename + " (modified)");
            }

        }
        return ret;
    }

    /**
     * return untracked\tracked filenames
     */
    public static ArrayList<String> getFilesNames(String mode) {
        Commit currentCommit = Methods.readHEADAsCommit();
        List<String> files = plainFilenamesIn(Repository.CWD);
        if (files == null) {
            return null;
        }
        ArrayList<String> ret = new ArrayList<>();
        for (String f : files) {
            File file = join(Repository.CWD, f);
            boolean flag = git().isTracked(file, currentCommit);
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
