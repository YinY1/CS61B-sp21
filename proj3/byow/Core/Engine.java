package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.time.LocalTime;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 81;
    public static final int HEIGHT = 61;
    TERenderer ter = new TERenderer();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
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
        int begin = input.indexOf("N");
        if(begin==-1){
            begin=input.indexOf("n");
        }
        int end = input.indexOf("S");
        if(end==-1){
            end=input.indexOf("s");
        }

        // if commit to autograder, use these two contents
        /*long seed = Long.parseLong(input, begin+1, end, 10);
        World world = new World(seed, WIDTH, HEIGHT);*/

        // if debug all play locally ,use these
        ter.initialize(WIDTH, HEIGHT);
        //long seed = Long.parseLong(input, begin+1, end, 10);
        long seed = LocalTime.now().toNanoOfDay();
        World world = new World(seed, WIDTH, HEIGHT);
        ter.renderFrame(world.tiles);

        return world.tiles;
    }


}
