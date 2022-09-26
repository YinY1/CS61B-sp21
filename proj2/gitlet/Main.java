package gitlet;

import java.io.IOException;

import static gitlet.Methods.*;

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
            Exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                init();
                break;
            case "add":
                add(args);
                break;
            // TODO: FILL THE REST IN
            case "rm":
                break;
            case "commit":
                commit(args);
                break;
            default:
                Exit("No command with that name exists.");
        }
    }
}
