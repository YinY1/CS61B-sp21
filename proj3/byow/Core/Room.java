package byow.Core;

import byow.Core.World.Point;
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
    private static final int TIMES = Engine.HEIGHT / 2;

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
        while (world.tiles[x][y] != Tileset.NOTHING || isCoveredRoom(world.rooms, x, y, w, h)) {
            x = world.getRandomX(w);
            y = world.getRandomY(h);
        }

        world.roomAreas.put(new Point(x, y), new Point(x + w - 1, y + h - 1));

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                world.tiles[i][j] = Tileset.ROOM;
                world.rooms[i][j] = true;
            }
        }
    }

    /**
     * test if a point{x,y} locates in a room
     */
    public static boolean isCoveredRoom(boolean[][] rooms, int x, int y, int w, int h) {
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

    public static boolean isRoom(boolean[][] rooms, World.Point point) {
        return rooms[point.x][point.y];
    }

    public static Point getRandomConnectionPoint(World world, Point bottomLeft, Point topRight) {
        int xl = bottomLeft.x;
        int yl = bottomLeft.y;
        int xr = topRight.x;
        int yr = topRight.y;

        int x = world.getRANDOM().nextInt(xr + 2 - (xl - 1)) + xl - 1;
        int y = world.getRANDOM().nextInt(yr + 2 - (yl - 1)) + yl - 1;
        Point ret = new Point(x, y);
        while (!world.connections.contains(ret)) {
            ret = new Point(world.getRANDOM().nextInt(xr + 2 - (xl - 1)) + xl - 1
                    , world.getRANDOM().nextInt(yr + 2 - (yl - 1)) + yl - 1);
        }
        return ret;
    }

    public static void addRoomsToRoot(World world) {
        world.roomAreas.keySet().forEach(r -> world.root.put(r, r));
    }

    public static Point getBottomLeft(World world, Point unit) {
        int x = unit.x;
        int y = unit.y;
        while (world.rooms[x][y]) {
            x--;
        }
        x++;
        while (world.rooms[x][y]) {
            y--;
        }
        y++;
        world.tiles[x][y] = Tileset.SAND;
        return new Point(x, y);
    }
}
