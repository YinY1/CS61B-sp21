package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

import static gitlet.Repository.BRANCHES_DIR;
import static gitlet.Utils.join;

/**
 * Represents gitlet branch pointer object.
 *
 * @author Edward Tsang
 */
public class Branch implements Serializable {
    /**
     * The branch name.
     */
    private final String name;
    /**
     * The HEAD commit uid
     */
    private String HEAD;

    /**
     * Instantiate a branch object with a branch name and HEAD commit uid.
     * Exit when a branch with the same name already exists.
     */
    public Branch(String name, String head) {
        if (isExists(name)) {
            Methods.exit("A branch with that name already exists.");
        }
        this.name = name;
        this.HEAD = head;
    }

    /**
     * Test whether branch with given name exists.
     */
    public static boolean isExists(String name) {
        List<String> names = Utils.plainFilenamesIn(BRANCHES_DIR);
        return names != null && names.contains(name);
    }

    /**
     * Read branch object with given name
     */
    public static Branch readBranch(String name) {
        File b = join(BRANCHES_DIR, name);
        return !b.exists() ? null : Utils.readObject(b, Branch.class);
    }

    /**
     * Writes current HEAD to this branch
     */
    public void updateBranch() {
        this.HEAD = Utils.readObject(Repository.HEAD, Branch.class).getHEADAsString();
        File h = join(BRANCHES_DIR, this.name);
        Utils.writeObject(h, this);
    }

    /**
     * Remove branch with given name
     *
     * @return true if and only if branch exists and is successfully deleted,
     * false otherwise.
     */
    public boolean remove(String branchName) {
        File b = join(BRANCHES_DIR, branchName);
        return b.delete();
    }

    public LinkedHashSet<String> findAllAncestors() {
        LinkedHashSet<String> ret = new LinkedHashSet<>();
        Commit c = Methods.toCommit(HEAD);
        while (c != null) {
            ret.add(c.getUid());
            c = c.getParentAsCommit();
        }
        return ret;
    }

    public void setHEADContent(String content) {
        this.HEAD = content;
    }

    public String getHEADAsString() {
        return this.HEAD;
    }

    public Commit getHEADAsCommit() {
        return Methods.toCommit(this.HEAD);
    }

    @Override
    public String toString() {
        return name;
    }
}
