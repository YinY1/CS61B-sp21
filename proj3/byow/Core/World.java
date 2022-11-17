package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents the rouge-like world.
 *
 * @author Edward Tsang
 */
public class World {
    final TETile[][] tiles;
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
        initializeWorld();
    }

    private void initializeWorld() {
        clear();
        Room.createRooms(this);
        Wall.createWalls(this);
        Road.createRoad(this);

        initializeAreas();
        Wall.findConnection(this);
        Wall.connectAreas(this);
        Road.removeDeadEnds(this);
        Wall.buildWallNearUnit(this);
    }

    private void initializeAreas() {
        root.clear();
        Road.addRoadsToArea(this);
        Room.addRoomsToArea(this);
        mainArea = (Room.getRandomRoom(this));
    }

    public void clear() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
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

    public boolean isWall(int x, int y) {
        return tiles[x][y] == Tileset.WALL;
    }

    public boolean isRoom(int x, int y) {
        return tiles[x][y] == Tileset.ROOM;
    }

    public boolean isRoad(int x, int y) {
        return tiles[x][y] == Tileset.FLOOR;
    }

    public boolean isUnit(int x, int y) {
        return !isWall(x, y) && !isNothing(x, y);
    }
}
