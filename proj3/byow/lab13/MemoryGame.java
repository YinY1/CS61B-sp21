package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.time.LocalTime;
import java.util.Random;

public class MemoryGame {
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};
    /**
     * The width of the window of this game.
     */
    private final int width;
    /**
     * The height of the window of this game.
     */
    private final int height;
    /**
     * The Random object used to randomly generate Strings.
     */
    private final Random rand;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        initDraw();

        this.rand = new Random(seed);
        this.round = 1;
        this.gameOver = false;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    private static double getTime() {
        return LocalTime.now().getSecond() + LocalTime.now().getNano() / 1000000000.0;
    }

    private void initDraw() {
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public String generateRandomString(int n) {
        StringBuilder s = new StringBuilder();
        while (n-- > 0) {
            s.append(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
        }
        return s.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(this.width / 2.0, this.height - 1.5, "Round " + this.round);
        StdDraw.text(this.width / 2.0, this.height / 2.0, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (char s : letters.toCharArray()) {
            StdDraw.clear(StdDraw.BLACK);
            drawFrame(String.valueOf(s));
            double start = getTime();
            while (getTime() - start < 1) {
            }
            StdDraw.clear(StdDraw.BLACK);
            drawFrame("");
            start = getTime();
            while (getTime() - start < 0.5) {
            }
        }

    }

    public String solicitNCharsInput(int n) {
        StringBuilder s = new StringBuilder();
        while (n-- > 0) {
            while (!StdDraw.hasNextKeyTyped()) {
            }
            s.append(StdDraw.nextKeyTyped());
            drawFrame(s.toString());

        }
        return s.toString();
    }

    public void startGame() {
        while (!gameOver) {
            String sequence = generateRandomString(round);
            flashSequence(sequence);
            while (StdDraw.hasNextKeyTyped()){
                StdDraw.nextKeyTyped();
            }
            if (!sequence.equals(solicitNCharsInput(sequence.length()))) {
                drawFrame("Game Over!");
                gameOver = true;
            } else {
                double start = getTime();
                while (getTime() - start < 1) {
                }
                drawFrame("√");
                while (getTime() - start < 2) {
                }
            }
            round++;
        }
    }
}
