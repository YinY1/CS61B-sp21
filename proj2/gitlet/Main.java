package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Edward Tsang
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            GitletUtils.exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> GitletUtils.init(args);
            case "add" -> GitletUtils.add(args);
            case "commit" -> GitletUtils.commit(args);
            case "rm" -> GitletUtils.remove(args);
            case "log" -> GitletUtils.log(args);
            case "global-log" -> GitletUtils.globalLog(args);
            case "find" -> GitletUtils.find(args);
            case "checkout" -> GitletUtils.checkout(args);
            case "status" -> GitletUtils.status(args);
            case "branch" -> GitletUtils.branch(args);
            case "rm-branch" -> GitletUtils.removeBranch(args);
            case "reset" -> GitletUtils.reset(args);
            case "merge" -> GitletUtils.merge(args);
            default -> GitletUtils.exit("No command with that name exists.");
        }
    }
}
