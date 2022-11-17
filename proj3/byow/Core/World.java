package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents the rouge-like world.
 *
 * @author Edward Tsang
 */
public class World {

    final ArrayList<Point> connections = new ArrayList<>();
    final TETile[][] tiles;
    final boolean[][] rooms;
    final boolean[][] roads;
    /**
     * store point of walls
     */
    final ArrayList<Point> walls = new ArrayList<>();
    /**
     * store point of walls and roads
     */
    final ArrayList<Point> units = new ArrayList<>();
    final HashMap<Point, Point> roomAreas = new HashMap<>();
    private final int width;
    private final int height;
    private final Random RANDOM;
    HashMap<Point, Point> root = new HashMap<>();
    Point mainArea;
    HashSet<Point> areas = new HashSet<>();

    World(long seed, int w, int h) {
        RANDOM = new Random(seed);
        width = w;
        height = h;
        tiles = new TETile[w][h];
        rooms = new boolean[w][h];
        roads = new boolean[w][h];
        clear();
        Room.createRooms(this);
        Wall.createWalls(this);
        Road.createRoad(this);
        Wall.findConnection(this);
        initializeAreas();
        Wall.connectAreas(this);
    }

    public void clear() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void initializeAreas() {
        root.clear();
        Road.addRoadsToArea(this);
        Room.addRoomsToArea(this);
        mainArea = (Room.getRandomRoom(this));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Random getRANDOM() {
        return RANDOM;
    }

    public int getRandomX(int w) {
        return RANDOM.nextInt((width - w - 1) / 2) * 2 + 1;
    }

    public int getRandomY(int h) {
        return RANDOM.nextInt((height - h - 1) / 2) * 2 + 1;
    }

    public boolean isNothing(int x, int y) {
        return tiles[x][y] == Tileset.NOTHING;
    }

}
