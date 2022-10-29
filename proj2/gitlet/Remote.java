package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

public class Remote implements Serializable {

    private final Map<String, File> remotes;

    Remote() {
        remotes = new HashMap<>();
    }

    public boolean addRemote(String name, File path) {
        if (isExists(name)) {
            return false;
        }
        remotes.put(name, path);
        save();
        return true;
    }

    public boolean removeRemote(String name) {
        if (!isExists(name)) {
            return false;
        }
        remotes.remove(name);
        save();
        return true;
    }

    public boolean isExists(String name) {
        return remotes.containsKey(name);
    }

    public File findRemote(String name) {
        return remotes.get(name);
    }

    public void fetch(String remoteName, Branch b) {
        File target = remotes.get(remoteName);
        if (!target.exists()) {
            Methods.exit("Remote directory not found.");
        }
        if (b == null) {
            Methods.exit("That remote does not have that branch.");
        }

        File objectsDir = join(target, "objects");
        Set<String> commits = new LinkedHashSet<>();
        Merge.findAllAncestors(b.getHEADAsCommitInRemote(objectsDir), commits);
        for (String commit : commits) {
            // copy commit object
            File sourcePath = join(objectsDir, commit.substring(0, 2));
            getObjectsDir(commit).mkdir();
            sourcePath = join(sourcePath, commit.substring(2));
            Methods.copy(sourcePath, makeObjectDir(commit));

            //copy blob objects
            Commit c = Methods.toCommit(commit, objectsDir);
            for (String blobID : c.getBlobs().values()) {
                String blobDir = blobID.substring(0, 2);
                String blobName = blobID.substring(2);
                File targetDir = join(OBJECTS_DIR, blobDir);
                targetDir.mkdir();
                sourcePath = join(objectsDir, blobDir, blobName);
                Methods.copy(sourcePath, join(targetDir, blobName));
            }
        }


        Branch nb = new Branch(remoteName + "/" + b, b.getHEADAsString());
        writeObject(join(Repository.BRANCHES_DIR, nb.toString()), nb);
    }

    public void save() {
        Utils.writeObject(Repository.REMOTES, this);
    }
}
