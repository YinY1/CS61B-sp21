package gitlet;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Methods.*;
import static gitlet.Repository.ADDITION_DIR;
import static gitlet.Repository.REMOVAL_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Status {

    public static void printStatus() {
        System.out.println("=== Branches ===");
        for (String b : getBranchesNames()) {
            System.out.println(b);
        }
        printFilenames("\n=== Staged Files ===", ADDITION_DIR);

        printFilenames("\n=== Removed Files ===", REMOVAL_DIR);

        System.out.println("\n=== Modifications Not Staged For Commit ===");


        System.out.println("\n=== Untracked Files ===");
        for (String u : getFilesNames("untracked")) {
            System.out.println(u);
        }
        System.out.println();
    }

    private static void printFilenames(String msg, File dir) {
        System.out.println(msg);
        List<String> files = plainFilenamesIn(dir);
        if (files != null) {
            for (String a : files) {
                System.out.println(a);
            }
        }
    }

    public static ArrayList<String> getBranchesNames() {
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll(plainFilenamesIn(Repository.BRANCHES_DIR));
        String name = Methods.readHEADAsBranch().getName();
        ret.remove(name);
        ret.add(0, "*" + name);
        return ret;
    }

    public static ArrayList<String> getModifiedButNotStagedFilesNames() {
        List<String> files = plainFilenamesIn(Repository.CWD);
        if (files == null) {
            return null;
        }
        ArrayList<String> ret = new ArrayList<>();
        Commit h = readHEADAsCommit();
        // TODO: get delete file
        for (String name : files) {
            File f = join(Repository.CWD, name);
            if ((isTracked(f, h) && isModified(f, h) && !isStaged(f))
                    || (isStaged(f) && isModified(f, h))) {
                ret.add(name + " (modified)");
            } else if (!f.exists() && (isStaged(f) || (!isRemoved(f) && isTracked(f, h)))) {
                ret.add(name + " (deleted)");
            }
        }
        return ret;
    }

    public static ArrayList<String> getFilesNames(String mode) {
        Commit currentCommit = Methods.readHEADAsCommit();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<String> ret = new ArrayList<>();
        files.addAll(plainFilenamesIn(Repository.CWD));
        for (String f : files) {
            File file = join(Repository.CWD, f);
            boolean flag = isTracked(file, currentCommit);
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

    public static boolean isTrackedAndModifiedNotStaged(File inFile) {
        Commit c = Methods.readHEADAsCommit();
        return isTracked(inFile, c)
                && !Methods.isModified(inFile, c)
                && !Methods.isStaged(inFile);
    }

}
