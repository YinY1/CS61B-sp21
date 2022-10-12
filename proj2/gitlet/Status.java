package gitlet;


import java.io.File;
import java.util.ArrayList;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Status {

    public static void printStatus() {
        System.out.println("=== Branches ===");
        for (String b : getBranchesNames()) {
            System.out.println(b);
        }
        System.out.println("\n=== Staged Files ===");
        for (String a : plainFilenamesIn(Repository.ADDITION_DIR)) {
            System.out.println(a);
        }
        System.out.println("\n=== Removed Files ===");
        for (String a : plainFilenamesIn(Repository.REMOVAL_DIR)) {
            System.out.println(a);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");


        System.out.println("\n=== Untracked Files ===");
        for (String u : getFilesNames("untracked")) {
            System.out.println(u);
        }
        System.out.println();
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
        ArrayList ret = new ArrayList<>();
        ret.addAll(plainFilenamesIn(Repository.CWD));

        return null;
    }

    public static ArrayList<String> getFilesNames(String mode) {
        Commit currentCommit = Methods.readHEADAsCommit();
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll(plainFilenamesIn(Repository.CWD));
        for (String f : ret) {
            File file = join(Repository.CWD, f);
            boolean flag = currentCommit.isTracked(file);
            if (mode.equals("untracked")) {
                flag = !flag;
            } else if (!mode.equals("tracked")) {
                Methods.exit("DEBUG HERE");
            }
            if (!flag) {
                ret.remove(file.getName());
            }
        }
        return ret;
    }

    public static boolean isTrackedAndModifiedNotStaged(File inFile) {
        Commit c = Methods.readHEADAsCommit();
        if (!c.isTracked(inFile)) {
            return false;
        }
        if (!Repository.isStaged(inFile)) {
            return false;
        }
        return true;
    }
}
