package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Merge.findAllAncestors;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;

/**
 * Represents gitlet remote object.
 *
 * @author Edward Tsang
 */
public class Remote implements Serializable {

    /**
     * KEY is the name of the remote
     * VALUE is the path of the remote
     */
    private final Map<String, File> remotes;

    Remote() {
        remotes = new HashMap<>();
    }

    /**
     * move all the new objects in given branch
     * from the repo where the branch in to target repo
     */
    private static void moveObjects(File sourceObjectsDir, File targetObjectsDir,
                                    Branch branch, Set<String> ancestors) {
        String branchHEAD = branch.getHEADAsString();
        for (String commit : ancestors) {
            moveObject(sourceObjectsDir, targetObjectsDir, commit);
            Methods.toCommit(commit, targetObjectsDir)
                    .getBlobs()
                    .values()
                    .forEach(objID -> moveObject(sourceObjectsDir, targetObjectsDir, objID));
            if (commit.equals(branchHEAD)) {
                break;
            }
        }
    }

    /**
     * move object (a commit or a blob) from source repo to target repo
     *
     * @param id the id of the object to move
     */
    private static void moveObject(File sourceObjectsDir, File targetObjectsDir, String id) {
        String dir = id.substring(0, 2);
        String name = id.substring(2);
        File targetDir = join(targetObjectsDir, dir);
        targetDir.mkdir();
        File sourcePath = join(sourceObjectsDir, dir, name);
        writeContents(join(targetDir, name), readContents(sourcePath));
    }

    /**
     * Brings down commits from the remote Gitlet repository into the local Gitlet repository.
     * Basically, this copies all commits and blobs from the given branch in the remote repository
     * (that are not already in the current repository) into a branch named
     * [remote name]/[remote branch name] in the local .gitlet (just as in real Git),
     * changing [remote name]/[remote branch name] to point to the head commit
     * (thus copying the contents of the branch from the remote repository to the current one).
     * This branch is created in the local repository if it did not previously exist.
     */
    public void fetch(String remoteName, Branch branch) {
        File sourceRepo = remotes.get(remoteName);
        if (branch == null) {
            Methods.exit("That remote does not have that branch.");
        }

        //get all commits in remote's active branch.
        File sourceObjectsDir = join(sourceRepo, "objects");
        Commit sourceHEAD = Methods.toCommit(
                readObject(join(sourceRepo, "HEAD"), Branch.class).getHEADAsString(),
                sourceObjectsDir);
        Set<String> ancestors = new LinkedHashSet<>();
        findAllAncestors(sourceHEAD, ancestors);

        // move these commits to current repo, fetch it
        moveObjects(sourceObjectsDir, OBJECTS_DIR, branch, ancestors);
        Branch nb = new Branch(remoteName + "/" + branch, branch.getHEADAsString());
        writeObject(join(Repository.BRANCHES_DIR, nb.toString()), nb);
    }

    /**
     * Attempts to append the current branch’s commits to
     * the end of the given branch at the given remote.
     */
    public void push(String remoteName, Branch branch) {
        //get all commits in current active branch.
        File target = remotes.get(remoteName);
        Set<String> ancestors = new LinkedHashSet<>();
        Commit currentHEAD = Methods.readHEADAsCommit();
        findAllAncestors(currentHEAD, ancestors);
        String branchHEAD = branch.getHEADAsString();
        if (!ancestors.contains(branchHEAD)) {
            Methods.exit("Please pull down remote changes before pushing.");
        }

        // move these commits to remote repo, push it
        moveObjects(OBJECTS_DIR, join(target, "objects"), branch, ancestors);
        Methods.setHEAD(currentHEAD, Methods.readHEADAsBranch(), target);
    }

    /**
     * Saves the given login information under the given remote name.
     * Attempts to push or pull from the given remote name will then
     * attempt to use this .gitlet directory.
     */
    public boolean addRemote(String name, File path) {
        if (isExists(name)) {
            return false;
        }
        remotes.put(name, path);
        save();
        return true;
    }

    /**
     * Remove information associated with the given remote name.
     * The idea here is that if you ever wanted to change a remote that you added,
     * you would have to first remove it and then re-add it.
     */
    public boolean removeRemote(String name) {
        if (!isExists(name)) {
            return false;
        }
        remotes.remove(name);
        save();
        return true;
    }

    /**
     * test if the remote with such name exists.
     */
    public boolean isExists(String name) {
        return remotes.containsKey(name);
    }

    public File getRemote(String name) {
        return remotes.get(name);
    }

    /**
     * serialize remote object.
     */
    public void save() {
        Utils.writeObject(Repository.REMOTES, this);
    }
}
