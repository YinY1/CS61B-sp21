package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.restrictedDelete;

/**
 * Represents a gitlet index object, gitlet-add, gitlet-rm.
 *
 * @author Edward Tsang
 */
public class Index implements Serializable {
    /**
     * The map of staged files.
     * File absolute path as KEY,
     * Blob name as VALUE.
     */
    private final Map<String, String> added;
    /**
     * The set of removed files.
     */
    private final Set<String> removed;
    /**
     * The set of tracked but not commits files.
     */
    private final Set<String> tracked;

    /**
     * Instantiate an index object.
     * An index is an object stored pointers of staged files, removed files and tracked files.
     */
    public Index() {
        added = new HashMap<>();
        removed = new HashSet<>();
        tracked = new HashSet<>();
    }

    /**
     * Test file is whether modified or not in given commit.
     *
     * @return true if and only if file exists and
     * isn't modified compare to the status of given commit.
     */
    public static boolean isModified(File inFile, Commit c) {
        if (!inFile.exists()) {
            return true;
        }
        String current = Blob.getBlobName(inFile);
        String oldBlobName = c.getBlob(inFile);
        return oldBlobName == null || !oldBlobName.equals(current);
    }

    public static boolean isModified(File inFile, Commit current, Commit target) {
        String cur = current.getBlob(inFile);
        String tar = target.getBlob(inFile);
        return !Objects.equals(tar, cur);
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry
     * in the staging area with the new contents.
     * The staging area should be somewhere in .gitlet.
     * If the current working version of the file is
     * identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal, if it was at the time of the command.
     */
    public void add(File file) {
        String f = file.getAbsolutePath();
        if (isRemoved(file)) {
            removed.remove(f);
        }
        if (isModified(file, Methods.readHEADAsCommit())) {
            added.put(f, new Blob(file).makeBlob());
            tracked.add(f);
        }
        save();
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so.
     */
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
        save();
        return flag;
    }

    /**
     * Clear index.added, index.removed, index.tracked after gitlet-commit then save the index.
     */
    public void cleanStagingArea() {
        added.clear();
        removed.clear();
        tracked.clear();
        save();
    }

    /**
     * Write index object.
     */
    public void save() {
        Utils.writeObject(Repository.INDEX, this);
    }

    /**
     * Test file is whether removed or not.
     */
    public boolean isRemoved(File inFile) {
        return removed.contains(inFile.getAbsolutePath());
    }

    /**
     * Test file is whether staged or not.
     */
    public boolean isStaged(File inFile) {
        return added.containsKey(inFile.getAbsolutePath());
    }

    /**
     * Test whether given file is tracked but not commit.
     */
    private boolean isTracked(File file) {
        return tracked.contains(file.getAbsolutePath());
    }

    /**
     * Test whether given file is tracked.
     */
    public boolean isTracked(File file, Commit c) {
        return c.getBlob(file) != null || isTracked(file);
    }

    public boolean isCommitted() {
        return added.isEmpty() && removed.isEmpty();
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    /**
     * Get filenames in staged area.
     */
    public Set<String> getAddedFilenames() {
        Set<String> ret = new HashSet<>();
        added.keySet().forEach(n -> ret.add(join(n).getName()));
        return ret;
    }

    /**
     * Get filenames in removed area.
     */
    public Set<String> getRemovedFilenames() {
        Set<String> ret = new HashSet<>();
        removed.forEach(n -> ret.add(join(n).getName()));
        return ret;
    }
}
