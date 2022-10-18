package gitlet;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Represents gitlet-log, gitlet-global-log.
 *
 * @author Edward Tsang
 */
public class Log {
    /**
     * Starting at the current head commit,
     * display information about each commit
     * backwards along the commit tree until the initial commit,
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
        Commit.findAll().forEach(Log::printLog);
    }

    /**
     * Display log in this format
     * <br><br>
     * ===
     * <br>
     * commit {commit id}
     * <br>
     * Date: E MMM dd HH:mm:ss yyyy Z {commit timestamp}
     * <br>
     * {commit message}
     */
    private static void printLog(Commit c) {
        System.out.println("===");
        System.out.println("commit " + c.getUid());
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        System.out.println("Date: " + d.format(c.getDate()));
        System.out.println(c.getLog() + "\n");
    }
}
