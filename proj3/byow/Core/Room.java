package byow.Core;

import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static com.google.common.primitives.Ints.min;

/**
 * Represents rooms generation.
 *
 * @author Edward Tsang
 */
public class Room {
    /**
     * try how many times to generate rooms
     */
    private static final int TIMES = min(Engine.HEIGHT, Engine.WIDTH) / 2;
    /**
     * minimum width of a room, must be odd
     */
    private static final int MIN_WIDTH = 5;
    /**
     * minimum height of a room, must be odd
     */
    private static final int MIN_HEIGHT = 5;
    private static final HashMap<Point, Point> roomAreas = new HashMap<>();
    public static void createRooms(World world) {
        for (int i = 0; i < TIMES; i++) {
            createRoom(world);
        }
    }

    private static void createRoom(World world) {
        int h = world.getRANDOM().nextInt(3) * 2 + MIN_HEIGHT;
        int w = world.getRANDOM().nextInt(3) * 2 + MIN_WIDTH;

        int x = world.getRandomX(w);
        int y = world.getRandomY(h);
        while (!world.isNothing(x, y) || isCoveredRoom(world, x, y, w, h)) {
            x = world.getRandomX(w);
            y = world.getRandomY(h);
        }

        roomAreas.put(new Point(x, y), new Point(x + w - 1, y + h - 1));

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                world.tiles[i][j] = Tileset.ROOM;
            }
        }
    }

    /**
     * test if a point{x,y} locates in a room
     */
    private static boolean isCoveredRoom(World world, int x, int y, int w, int h) {
        if (x == 0) {
            x++;
        }
        if (y == 0) {
            y++;
        }
        // The scope of the room includes a tile outside and near the room walls.
        for (int i = x - 1; i < x + w + 1; i++) {
            if (world.isRoom(i,y - 1) || world.isRoom(i,y + h)) {
                return true;
            }
        }
        for (int j = y - 1; j < y + h + 1; j++) {
            if (world.isRoom(x - 1,j) || world.isRoom(x + w,j)) {
                return true;
            }
        }
        return false;
    }

    public static void addRoomsToArea(World world) {
        Set<Point> keys = roomAreas.keySet();
        world.areas.addAll(keys);
        keys.forEach(r -> world.root.put(r, r));
    }

    /**
     * get the bottom left point of a room, which represents the room area
     */
    public static Point getBottomLeft(World world, Point unit) {
        int x = unit.x;
        int y = unit.y;
        while (world.isRoom(x,y)) {
            x--;
        }
        x++;
        while (world.isRoom(x,y)) {
            y--;
        }
        y++;
        return new Point(x, y);
    }

    public static Point getRandomRoom(World world) {
        ArrayList<Point> keys = new ArrayList<>(roomAreas.keySet());
        int idx = world.getRANDOM().nextInt(keys.size());
        Point room = keys.get(idx);
        if (world.mainArea != null && Point.isInMainArea(world, room)) {
            return null;
        }
        return room;
    }

    public static int getRoomAreasNum(){
        return roomAreas.size();
    }
}
