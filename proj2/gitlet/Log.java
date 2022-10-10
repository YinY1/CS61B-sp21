package gitlet;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Represents a gitlet log object
 *
 * @author Edward Tsang
 */
public class Log {
    /**
     * Starting at the current head commit,
     * display information about each commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second parents found in merge commits.
     */
    public static void log(Commit c) {
        while (c != null) {
            printLog(c);
            c = Commit.findWithUid(c.getParentAsString());
        }
    }

    /**
     * Displays information about all commits ever made.
     */
    public static void globalLog() {
        List<Commit> commits = Commit.findAll();
        for (Commit commit : commits) {
            printLog(commit);
        }
    }

    /**
     * Display log in this format
     * <br><br>
     * ===
     * <br>
     * commit ___the uid of the commit___
     * <br>
     * Date: E MMM dd HH:mm:ss yyyy Z ___the timestamp___
     * <br>
     * ___the message___
     */
    private static void printLog(Commit c) {
        System.out.println("===");
        System.out.println("commit " + c.getUid());
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        System.out.println("Date: " + d.format(c.getDate()));
        System.out.println(c.getLog() + "\n");
    }
}
