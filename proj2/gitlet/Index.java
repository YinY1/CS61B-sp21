package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.join;
import static gitlet.Utils.restrictedDelete;

public class Index implements Serializable {
    private final Map<String, String> added;
    private final Set<String> removed;

    private final Set<String> tracked;

    public Index() {
        added = new HashMap<>();
        removed = new HashSet<>();
        tracked = new HashSet<>();
    }

    public void add(File file) {
        String f = file.getAbsolutePath();
        // if removed, delete the rm
        if (isRemoved(file)) {
            removed.remove(f);
        }
        if (isModified(file, Methods.readHEADAsCommit())) {
            added.put(f, new Blob(file).makeBlob());
            tracked.add(f);
        }
        stage();
    }

    public boolean remove(File file) {
        boolean flag = false;
        String f = file.getAbsolutePath();
        if (isStaged(file)) {
            added.remove(f);
            flag = true;
        }
        if (!flag && isTracked(file, Methods.readHEADAsCommit())) {
            removed.add(f);
            restrictedDelete(f);
            flag = true;
        }
        stage();
        return flag;
    }

    public void cleanStagingArea() {
        added.clear();
        removed.clear();
        tracked.clear();
        stage();
    }

    public void stage() {
        Utils.writeObject(Repository.INDEX, this);
    }

    public boolean isRemoved(File inFile) {
        return removed.contains(inFile.getAbsolutePath());
    }

    public boolean isStaged(File inFile) {
        return added.containsKey(inFile.getAbsolutePath());
    }

    public boolean isModified(File inFile, Commit c) {
        if (!inFile.exists()) {
            return false;
        }
        String current = Blob.getBlobName(inFile);
        String oldBlobName = c.getBlobs().get(inFile.getAbsolutePath());
        return oldBlobName == null || !oldBlobName.equals(current);
    }

    public boolean isTracked(File file) {
        return tracked.contains(file.getAbsolutePath());
    }

    public boolean isTracked(File file, Commit c) {
        return c.getBlobs().get(file.getAbsolutePath()) != null || isTracked(file);
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public Set<String> getAddedFilenames() {
        Set<String> ret = new HashSet<>();
        added.keySet().forEach(n -> ret.add(join(n).getName()));
        return ret;
    }

    public Set<String> getRemovedFilenames() {
        Set<String> ret = new HashSet<>();
        removed.forEach(n -> ret.add(join(n).getName()));
        return ret;
    }
}
