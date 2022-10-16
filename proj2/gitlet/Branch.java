package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

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
        List<String> names = Utils.plainFilenamesIn(Repository.BRANCHES_DIR);
        return names != null && names.contains(name);
    }

    public static Branch readBranch(String name) {
        File b = Utils.join(Repository.BRANCHES_DIR, name);
        return Utils.readObject(b, Branch.class);
    }

    /**
     * Writes current HEAD to this branch
     */
    public void updateBranch() {
        this.HEAD = Utils.readObject(Repository.HEAD, Branch.class).getHEAD();
        File h = Utils.join(Repository.BRANCHES_DIR, this.name);
        Utils.writeObject(h, this);
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
