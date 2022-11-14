package byow.Core;

import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Represents rooms generation.
 *
 * @author Edward Tsang
 */
public class Room {
    /**
     * try how many times to generate rooms
     */
    private static final int TIMES = 36;

    public static void createRooms(World world) {
        for (int i = 0; i < TIMES; i++) {
            createRoom(world);
        }
    }

    private static void createRoom(World world) {
        Random random = world.getRANDOM();
        int h = random.nextInt(3) * 2 + 5;
        int w = random.nextInt(3) * 2 + 5;

        int x = world.getRandomX(w);
        int y = world.getRandomY(h);

        while (world.tiles[x][y] != Tileset.NOTHING || isRoom(world.rooms, x, y, w, h)) {
            x = world.getRandomX(w);
            y = world.getRandomY(h);
        }

        for (int i = x; i < x + w; i++) {
            world.tiles[i][y] = Tileset.ROOM;
            world.tiles[i][y + h - 1] = Tileset.ROOM;
            world.rooms[i][y] = true;
            world.rooms[i][y + h - 1] = true;
        }
        for (int j = y; j < y + h; j++) {
            world.tiles[x][j] = Tileset.ROOM;
            world.tiles[x + w - 1][j] = Tileset.ROOM;
            world.rooms[x][j] = true;
            world.rooms[x + w - 1][j] = true;
        }
        for (int i = x + 1; i < x + w - 1; i++) {
            for (int j = y + 1; j < y + h - 1; j++) {
                world.tiles[i][j] = Tileset.FLOOR;
                world.rooms[i][j] = true;
            }
        }

    }

    /**
     * test if a point{x,y} locates in a room
     */
    public static boolean isRoom(boolean[][] rooms, int x, int y, int w, int h) {
        if (x == 0) {
            x++;
        }
        if (y == 0) {
            y++;
        }
        // The scope of the room includes a tile outside and near the room walls.
        for (int i = x - 1; i < x + w + 1; i++) {
            if (rooms[i][y - 1] || rooms[i][y + h]) {
                return true;
            }
        }
        for (int j = y - 1; j < y + h + 1; j++) {
            if (rooms[x - 1][j] || rooms[x + w][j]) {
                return true;
            }
        }
        return false;
    }
}
