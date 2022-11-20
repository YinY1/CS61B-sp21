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
                inputSeed(input);
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
        input = fixInputString(input);
        if (!start) {
            if (input.contains("L")) {
                load();
                input = input.substring(1);
            } else {
                // if commit to autograder, use these two contents
                /*long seed = Long.parseLong(input, begin+1, end, 10);
                World world = new World(seed, WIDTH, HEIGHT);*/

                // if debug or play locally ,use these
                int end = input.indexOf('S') + 1;
                generateWorld(input.substring(0, end));
                input = input.substring(end);
            }
            start = true;
        }
        move(input);
        if (input.indexOf(':') > -1) {
            quit();
        }
        return tempWorld.tiles;
    }

    public String fixInputString(String input) {
        StringBuilder split = new StringBuilder();
        boolean loadFlag = false;
        boolean startFlag = false;
        boolean moveFlag = false;
        char[] in = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            switch (in[i]) {
                case 'l', 'L' -> {
                    loadFlag = true;
                    split.append(in[i]);
                    moveFlag = true;
                }
                case 'n', 'N' -> {
                    if (!loadFlag) {
                        startFlag = true;
                        split.append(in[i]);
                    }
                }
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    if (startFlag && !loadFlag) {
                        split.append(in[i]);
                    }
                }
                case 's', 'S' -> {
                    if (startFlag && !loadFlag
                            && (input.length() - input.lastIndexOf('n') > 1
                            || input.length() - input.lastIndexOf('N') > 1)) {
                        split.append(in[i]);
                        startFlag = false;
                        moveFlag = true;
                    } else if (moveFlag || start) {
                        split.append(in[i]);
                    }
                }
                case 'w', 'W', 'a', 'A', 'd', 'D' -> {
                    if (moveFlag || start) {
                        split.append(in[i]);
                    }
                }
                case ':' -> {
                    if (in[i + 1] == 'q' || in[i + 1] == 'Q') {
                        split.append(':');
                        split.append('Q');
                        return split.toString().toUpperCase();
                    }
                }
            }
        }
        return split.toString().toUpperCase();
    }

    /**
     * @param input N#S, # means the seed number
     */
    private void generateWorld(String input) {
        long seed = Long.parseLong(input, 1, input.length() - 1, 10);
        //long seed = LocalTime.now().toNanoOfDay();
        world.initializeWorld(seed);
        tempWorld = world.clone();
        characters = new Characters(tempWorld);
        characters.setCharacters(tempWorld, "");
    }

    private void inputSeed(StringBuilder input) {
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

    private void inputCommands(StringBuilder input) {
        boolean quit = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                switch (ch) {
                    case 'w', 'W', 's', 'S', 'a', 'A', 'd', 'D' -> {
                        if (quit) {
                            input.deleteCharAt(0);
                        }
                        input.append(ch);
                        return;
                    }
                    case ':' -> {
                        input.append(ch);
                        quit = true;
                    }
                    case 'q', 'Q' -> {
                        if (quit) {
                            input.append(ch);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void load() {
        start = true;
        //TODO
    }

    private void save() {
        System.out.println("Saved successfully!");
        //TODO
    }

    private void quit() {
        save();
        System.out.println("Quited successfully!");
        //TODO
    }

    private void move(String command) {
        for (char s : command.toCharArray()) {
            tempWorld = world.clone();
            characters.setCharacters(tempWorld, String.valueOf(s));
        }
    }
}
