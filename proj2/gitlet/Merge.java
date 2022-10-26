package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Checkout.checkoutFile;
import static gitlet.Index.isModified;
import static gitlet.Utils.*;

public class Merge {
    /**
     * Merges files from the given branch into the current branch.
     */
    public static void merge(Branch current, Branch given) {
        String split = getSplitPoint(current, given);
        if (given.getHEADAsString().equals(split)) {
            Methods.exit("Given branch is an ancestor of the current branch.");
        }
        if (current.getHEADAsString().equals(split)) {
            given.updateBranch();
            Methods.exit("Current branch fast-forwarded.");
        }

        Commit sp = Methods.toCommit(split);
        Commit cur = current.getHEADAsCommit();
        Commit tar = given.getHEADAsCommit();
        Set<String> files = new HashSet<>(cur.getBlobs().keySet());
        files.addAll(tar.getBlobs().keySet());
        List<String> cwd = plainFilenamesIn(Repository.CWD);
        if (cwd != null) {
            cwd.forEach(n -> files.add(join(Repository.CWD, n).getAbsolutePath()));
        }
        String msg = "Merged " + given + " into " + current + ".";
        doMerge(files, sp, cur, tar, msg);
    }

    private static String getSplitPoint(Branch current, Branch given) {
        List<String> splits = new ArrayList<>();
        Set<String> commits = new HashSet<>();
        dfs(current.getHEADAsCommit(), commits, splits);
        dfs(given.getHEADAsCommit(), commits, splits);
        /*LinkedHashSet<String> cur = current.findAllAncestors();
        LinkedHashSet<String> target = given.findAllAncestors();
        target.retainAll(cur);
        return new ArrayList<>(target).get(0);*/
        return splits.get(splits.size() - 1);
    }

    private static void dfs(Commit b, Set<String> commits, List<String> splits) {
        if (b == null) {
            return;
        }
        if (commits.contains(b.getUid())) {
            splits.add(b.getUid());
            return;
        }
        commits.add(b.getUid());
        dfs(b.getParentAsCommit(), commits, splits);
        dfs(b.getSecondParentAsCommit(), commits, splits);
    }

    private static void doMerge(Set<String> files, Commit split,
                                Commit current, Commit given, String msg) {
        Index idx = Methods.readStagingArea();
        files.forEach(f -> merge(split, current, given, idx, join(f)));
        new Commit(msg, current.getUid(), given.getUid()).makeCommit();
        //TODO: 2 parents
    }

    private static void merge(Commit split, Commit current, Commit given, Index idx, File f) {
        boolean flag = onlyPresentInCurrentBranch(f, current, given, split);
        flag = onlyAbsentInCurrentBranch(f, current, given, split, flag);
        flag = onlyPresentInGivenBranch(f, current, given, split, idx, flag);
        flag = onlyAbsentInGivenBranch(f, current, given, split, idx, flag);
        flag = onlyModifiedInGivenBranch(f, current, given, split, idx, flag);
        flag = onlyModifiedInCurrentBranch(f, current, given, split, flag);
        flag = modifiedInSame(f, current, given, split, flag);
        if (!flag && conflict(f, current, given, idx)) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Any files that have been modified in the given branch since the split point,
     * but not modified in the current branch since the split point
     * should be changed to their versions in the given branch
     * (checked out from the commit at the front of the given branch).
     * These files should then all be automatically staged.
     * To clarify, if a file is “modified in the given branch since the split point” this means
     * the version of the file as it exists in the commit at the front of the given branch
     * has different content from the version of the file at the split point.
     * Remember: blobs are content addressable!
     */
    private static boolean onlyModifiedInGivenBranch(File file, Commit current, Commit split,
                                                     Commit given, Index index, boolean flag) {
        if (flag) {
            return true;
        }
        if (!isModified(file, current, split)) {
            if (given.getBlob(file) == null) {
                index.remove(file);
                return true;
            } else if (isModified(file, given, split)) {
                checkoutFile(given, file);
                index.add(file);
                return true;
            }
        }
        return false;
    }

    /**
     * Any files that have been modified in the current branch
     * but not in the given branch since the split point should stay as they are.
     */
    private static boolean onlyModifiedInCurrentBranch(File file, Commit current,
                                                       Commit given, Commit split, boolean flag) {
        if (flag) {
            return true;
        }
        return isModified(file, current, split) && !isModified(file, given, split);
    }

    /**
     * Any files that have been modified in both the current and given branch in the same way
     * (i.e., both files now have the same content or were both removed)
     * are left unchanged by the merge.
     * If a file was removed from both the current and given branch,
     * but a file of the same name is present in the working directory,
     * it is left alone and continues to be absent (not tracked nor staged) in the merge.
     */
    private static boolean modifiedInSame(File file, Commit current,
                                          Commit given, Commit split, boolean flag) {
        if (flag) {
            return true;
        }
        return isModified(file, current, split)
                && Objects.equals(current.getBlob(file), given.getBlob(file));
    }

    /**
     * Any files that were not present at the split point
     * and are present only in the current branch should remain as they are.
     */
    private static boolean onlyPresentInCurrentBranch(File file, Commit current,
                                                      Commit given, Commit split) {
        return split.getBlob(file) == null
                && current.getBlob(file) != null && given.getBlob(file) == null;
    }

    /**
     * Any files that were not present at the split point
     * and are present only in the given branch should be checked out and staged.
     */
    private static boolean onlyPresentInGivenBranch(File file, Commit current, Commit given,
                                                    Commit split, Index index, boolean flag) {
        if (flag) {
            return true;
        }
        if (split.getBlob(file) == null && current.getBlob(file) == null
                && given.getBlob(file) != null) {
            checkoutFile(given, file);
            index.add(file);
            return true;
        }
        return false;
    }

    /**
     * Any files present at the split point,
     * unmodified in the current branch,
     * and absent in the given branch should be removed (and untracked).
     */
    private static boolean onlyAbsentInGivenBranch(File file, Commit current, Commit given,
                                                   Commit split, Index index, boolean flag) {
        if (flag) {
            return true;
        }
        if (split.getBlob(file) != null && !isModified(file, current, split)
                && given.getBlob(file) == null) {
            index.remove(file);
            return true;
        }
        return false;
    }

    /**
     * Any files present at the split point,
     * unmodified in the given branch,
     * and absent in the current branch should remain absent.
     */
    private static boolean onlyAbsentInCurrentBranch(File file, Commit current,
                                                     Commit given, Commit split, boolean flag) {
        if (flag) {
            return true;
        }
        return split.getBlob(file) != null && !isModified(file, given, split)
                && current.getBlob(file) == null;
    }

    /**
     * Any files modified in different ways in the current and given branches are in conflict.
     * “Modified in different ways” can mean that
     * the contents of both are changed and different from other,
     * or the contents of one are changed and the other file is deleted,
     * or the file was absent at the split point
     * and has different contents in the given and current branches.
     * In this case, replace the contents of the conflicted file with<br>
     * <br><<<<<<< HEAD
     * <br>contents of file in current branch
     * <br>=======
     * <br>contents of file in given branch
     * <br>>>>>>>><br>
     * <br>(replacing “contents of…” with the indicated file’s contents) and stage the result.
     * Treat a deleted file in a branch as an empty file.
     * Use straight concatenation here.
     * In the case of a file with no newline at the end,
     * you might well end up with something like this:<br>
     * <br><<<<<<< HEAD
     * <br>contents of file in current branch=======
     * <br>contents of file in given branch>>>>>>><br>
     * <br>This is fine; people who produce non-standard,
     * pathological files because they don’t know the difference between
     * a line terminator and a line separator deserve what they get.
     */
    private static boolean conflict(File file, Commit current, Commit given, Index index) {
        String cur = current.getBlob(file);
        String tar = given.getBlob(file);
        if (!Objects.equals(cur, tar)) {
            String curContent = "";
            String tarContent = "";
            if (cur != null) {
                curContent = Methods.toBlob(cur).getContent();
            }
            if (tar != null) {
                tarContent = Methods.toBlob(tar).getContent();
            }
            String content = "<<<<<<< HEAD\n" + curContent + "=======\n" + tarContent + ">>>>>>>\n";
            writeContents(file, content);
            index.add(file);
            return true;
        }
        return false;
    }
}
