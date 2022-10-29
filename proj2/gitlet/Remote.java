package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.join;

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
        String[] dirs = objectsDir.list();
        if (dirs != null) {
            for (String dir : dirs) {
                File curDir = join(Repository.OBJECTS_DIR, dir);
                curDir.mkdir();
                File objs = join(objectsDir, dir);
                List<String> objects = Utils.plainFilenamesIn(objs);
                if (objects != null) {
                    for (String object : objects) {
                        File obj = join(objs, object);
                        File cur = join(curDir, object);
                        if (!cur.exists()) {
                            Methods.copy(obj, cur);
                        }
                    }
                }
            }
        }

        Branch nb = new Branch(remoteName + "/" + b.toString(), Methods.readHEADContent());
        nb.updateBranch();
    }

    public void save() {
        Utils.writeObject(Repository.REMOTES, this);
    }
}
