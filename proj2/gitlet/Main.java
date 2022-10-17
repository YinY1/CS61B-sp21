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
            Methods.exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> Methods.init(args);
            case "add" -> Methods.add(args);
            case "commit" -> Methods.commit(args);
            case "rm" -> Methods.remove(args);
            case "log" -> Methods.log(args);
            case "global-log" -> Methods.globalLog(args);
            case "find" -> Methods.find(args);
            case "checkout" -> Methods.checkout(args);
            case "status" -> Methods.status(args);
            case "branch" -> Methods.branch(args);
            case "rm-branch" -> Methods.removeBranch(args);
            case "reset" -> Methods.reset(args);
            default -> Methods.exit("No command with that name exists.");
        }
    }
}
