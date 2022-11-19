package byow.Core;

import byow.Core.Character.Characters;
import byow.Core.HUD.Framework;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

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


    private static void inputSeed(StringBuilder input) {
        boolean firstN = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (!firstN && (ch == 'N' || ch == 'n')) {
                    input.append(ch);
                    firstN = true;
                }
                if (firstN) {
                    if (ch >= '0' && ch <= '9') {
                        input.append(ch);
                    }
                    if (ch == 's' || ch == 'S') {
                        input.append(ch);
                        break;
                    }
                }
            }
        }
    }

    private void inputMovement(StringBuilder input) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                switch (ch) {
                    case 'w', 'W', 's', 'S', 'a', 'A', 'd', 'D' -> {
                        input.append(ch);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT, 2, 2);

        Framework f = new Framework();
        f.drawMenu();

        while (true){
            StringBuilder input = new StringBuilder();
            if (!start) {
                inputSeed(input);
            } else {
                inputMovement(input);
            }
            TETile[][] tiles = interactWithInputString(input.toString());
            ter.renderFrame(tiles);
            f.drawFramework();
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
        if (!start) {
            // if commit to autograder, use these two contents
            /*long seed = Long.parseLong(input, begin+1, end, 10);
            World world = new World(seed, WIDTH, HEIGHT);*/

            // if debug or play locally ,use these
            long seed = Long.parseLong(input, 1, input.length() - 1, 10);
            //long seed = LocalTime.now().toNanoOfDay();
            world.initializeWorld(seed);
            tempWorld = world.clone();
            characters = new Characters(tempWorld);
            start = true;
        } else {
            tempWorld = world.clone();
            characters.setCharacters(tempWorld, input);
        }
        return tempWorld.tiles;
    }
}
