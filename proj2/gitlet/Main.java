package gitlet;

import java.io.IOException;

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
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            Methods.Exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> Methods.init();
            case "add" -> Methods.add(args);
            case "commit" -> Methods.commit(args);
            case "checkout" -> Methods.checkout(args);
            default -> Methods.Exit("No command with that name exists.");
        }
    }
}
