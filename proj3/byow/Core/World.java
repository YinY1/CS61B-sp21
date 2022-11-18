package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * Represents the rouge-like world.
 *
 * @author Edward Tsang
 */
public class World {
    final TETile[][] tiles;
    private final int width;
    private final int height;

    World(long seed, int w, int h) {
        width = w;
        height = h;
        tiles = new TETile[w][h];
        Variables v = new Variables(seed);
        initializeWorld(v);
    }

    private void initializeWorld(Variables v) {
        fillWithNothing();
        Room.createRooms(this, v);
        Wall.createWalls(this);
        Road.createRoad(this, v);

        initializeAreas(v);
        Wall.findConnection(this, v);
        Wall.connectAreas(this, v);
        Road.removeDeadEnds(this);
        Wall.buildWallNearUnit(this);
    }

    private void initializeAreas(Variables v) {
        v.root.clear();
        Road.addRoadsToArea(this, v);
        Room.addRoomsToArea(v);
        v.mainArea = (Room.getRandomRoom(v));
    }

    public void fillWithNothing() {
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

    public int getRandomX(int w, Variables v) {
        return v.RANDOM.nextInt((width - w - 1) / 2) * 2 + 1;
    }

    public int getRandomY(int h, Variables v) {
        return v.RANDOM.nextInt((height - h - 1) / 2) * 2 + 1;
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

    public boolean isDoor(int x, int y) {
        return tiles[x][y] == Tileset.UNLOCKED_DOOR;
    }
}
