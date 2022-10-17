package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Repository.BRANCHES_DIR;
import static gitlet.Utils.join;

public class Branch implements Serializable {
    private final String name;

    private String HEAD;

    public Branch(String name, String head) {
        if (isExists(name)) {
            Methods.exit("A branch with that name already exists.");
        }
        this.name = name;
        this.HEAD = head;
    }

    public static boolean isExists(String name) {
        List<String> names = Utils.plainFilenamesIn(BRANCHES_DIR);
        return names != null && names.contains(name);
    }

    public static Branch readBranch(String name) {
        File b = join(BRANCHES_DIR, name);
        return Utils.readObject(b, Branch.class);
    }

    /**
     * Writes current HEAD to this branch
     */
    public void updateBranch() {
        this.HEAD = Utils.readObject(Repository.HEAD, Branch.class).getHEAD();
        File h = join(BRANCHES_DIR, this.name);
        Utils.writeObject(h, this);
    }

    public boolean remove(String branchName) {
        File b = join(BRANCHES_DIR, branchName);
        if (!b.exists()) {
            return false;
        }
        b.delete();
        return true;
    }

    public void setHEADContent(String content) {
        this.HEAD = content;
    }

    public String getHEAD() {
        return this.HEAD;
    }

    public String getName() {
        return name;
    }
}
