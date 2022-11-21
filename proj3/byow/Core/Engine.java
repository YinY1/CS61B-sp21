package byow.Core;

import byow.Core.Character.Characters;
import byow.Core.HUD.Framework;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import static byow.Core.Utils.*;

public class Engine {
    /* IF you want to modify width smaller than 81
     * or height smaller than 61,
     * please do modify the minimum size of room as well */
    public static final int WIDTH = 81;
    public static final int HEIGHT = 61;
    public boolean start = false;
    TERenderer ter = new TERenderer();
    World world = new World(WIDTH - 3, HEIGHT - 3);
    World tempWorld;
    Characters characters;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT, 2, 2);

        Framework f = new Framework();
        f.drawMenu();

        while (true) {
            StringBuilder input = new StringBuilder();
            if (!start) {
                getStarted(input);
            } else {
                inputCommands(input);
            }
            TETile[][] tiles = interactWithInputString(input.toString());
            if (tiles == null) {
                return;
            }
            f.drawFramework(ter, tiles);
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        input = fixInputString(this, input);
        System.out.println(input);
        if (!start) {
            if (input.contains("L")) {
                load(this);
                input = input.substring(1);
            } else {
                // if commit to autograder, use these two contents
                /*long seed = Long.parseLong(input, begin+1, end, 10);
                World world = new World(seed, WIDTH, HEIGHT);*/

                // if debug or play locally ,use these
                int end = input.indexOf('S') + 1;
                generateWorld(this, input.substring(0, end));
                input = input.substring(end);
            }
            start = true;
        }
        move(this, input);
        if (input.indexOf(':') > -1) {
            quit(this);
        }
        return tempWorld.tiles;
    }
}
