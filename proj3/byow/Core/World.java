package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class World {
    private final int width;
    private final int height;
    private final Random RANDOM;
    private final TETile[][] tiles;
    private final boolean[][] rooms;

    World(long seed, int w, int h) {
        RANDOM = new Random(seed);
        tiles = new TETile[w][h];
        rooms = new boolean[w][h];
        width = w;
        height = h;
        clear();
        createRooms();
    }

    public void clear() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }


    private void createRoom() {
        int h = RANDOM.nextInt(3) * 2 + 5;
        int w = RANDOM.nextInt(3) * 2 + 5;

        int x = RANDOM.nextInt((width - w - 1) / 2) * 2 + 1;
        int y = RANDOM.nextInt((height - h - 1) / 2) * 2 + 1;
        while (tiles[x][y] != Tileset.NOTHING || isRoom(x, y, w, h)) {
            x = RANDOM.nextInt(width - w - 1);
            y = RANDOM.nextInt(height - h - 1);
        }

        for (int i = x; i < x + w; i++) {
            tiles[i][y] = Tileset.WALL;
            tiles[i][y + h - 1] = Tileset.WALL;
            rooms[i][y] = true;
            rooms[i][y + h - 1] = true;
        }
        for (int i = y; i < y + h; i++) {
            tiles[x][i] = Tileset.WALL;
            tiles[x + w - 1][i] = Tileset.WALL;
            rooms[x][i] = true;
            rooms[x + w - 1][i] = true;
        }
        for (int i = x + 1; i < x + w - 1; i++) {
            for (int j = y + 1; j < y + h - 1; j++) {
                tiles[i][j] = Tileset.FLOOR;
                rooms[i][j] = true;
            }
        }

    }

    private void createRooms() {
        for (int i = 0; i < 30; i++) {
            createRoom();
        }
    }

    private boolean isRoom(int x, int y, int w, int h) {
        for (int i = x; i < x + w; i++) {
            if(rooms[i][y]||rooms[i][y+h-1]){
                return true;
            }
        }
        for (int j = y; j < y + h; j++) {
            if(rooms[x][j]||rooms[x+w-1][j]){
                return true;
            }
        }
        return false;
    }

    public TETile[][] getTiles() {
        return tiles;
    }
}
