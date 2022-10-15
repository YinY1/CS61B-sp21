package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index implements Serializable {
    private final Map<String, String> added;
    private final List<String> removed;

    public Index() {
        added = new HashMap<>();
        removed = new ArrayList<>();
    }

    public void add(File file) {
        String f = file.getAbsolutePath();
        // if removed, delete the rm
        if (isRemoved(file)) {
            removed.remove(f);
        }
        // write blob object
        Blob b = new Blob(file);
        added.put(f, b.makeBlob());
    }

    public boolean remove(File file) {
        boolean flag = false;
        String f = file.getAbsolutePath();
        if (isStaged(file)) {
            added.remove(f);
            flag = true;
        }
        if (isTracked(file, Methods.readHEADAsCommit())) {
            removed.add(f);
            flag = true;
        }
        return flag;
    }

    public void cleanStagingArea() {
        added.clear();
        removed.clear();
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

    public boolean isModified(File inFile) {
        String current = Blob.getBlobName(inFile);
        return added.get(inFile.getAbsolutePath()).equals(current);
    }

    public boolean isTracked(File file, Commit c) {
        return c.getBlobs().get(file.getAbsolutePath()) != null || Methods.isStaged(file);
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public List<String> getRemoved() {
        return removed;
    }
}
