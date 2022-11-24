package byow.Core.Maps;

import byow.Core.Engine;
import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
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

    public static void createRooms(World world, Variables v) {
        for (int i = 0; i < TIMES; i++) {
            createRoom(world, v);
        }
    }

    private static void createRoom(World world, Variables v) {
        int h = v.getRANDOM().nextInt(3) * 2 + MIN_HEIGHT;
        int w = v.getRANDOM().nextInt(3) * 2 + MIN_WIDTH;

        int x = world.getRandomX(w, v);
        int y = world.getRandomY(h, v);
        int count = 0;
        while (!world.isNothing(x, y) || isCoveredRoom(world, x, y, w, h)) {
            x = world.getRandomX(w, v);
            y = world.getRandomY(h, v);
            count++;
            if (count == TIMES) {
                return;
            }
        }

        v.getRoomAreas().put(new Point(x, y), new Point(x + w - 1, y + h - 1));

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                world.getTiles()[i][j] = Tileset.ROOM;
            }
        }
    }

    /**
     * test if a point{x,y} locates in a room
     */
    private static boolean isCoveredRoom(World world, int x, int y, int w, int h) {
        if (world.isRoom(x, y)) {
            return true;
        }
        if (x == 0) {
            x++;
        }
        if (y == 0) {
            y++;
        }
        // The scope of the room includes a tile outside and near the room walls.
        for (int i = x - 1; i < x + w + 1; i++) {
            for (int j = y - 1; j < y + h + 1; j++) {
                if (world.isRoom(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addRoomsToArea(Variables v) {
        Set<Point> keys = v.getRoomAreas().keySet();
        v.getAreas().addAll(keys);
        keys.forEach(r -> v.getRoot().put(r, r));
    }

    /**
     * get the bottom left point of a room, which represents the room area
     */
    public static Point getBottomLeft(World world, Point unit) {
        int x = unit.getX();
        int y = unit.getY();
        while (world.isRoom(x, y)) {
            x--;
        }
        x++;
        while (world.isRoom(x, y)) {
            y--;
        }
        y++;
        return new Point(x, y);
    }

    public static Point getRandomRoom(Variables v) {
        ArrayList<Point> rooms = new ArrayList<>(v.getRoomAreas().keySet());
        Point room = rooms.get(v.getRANDOM().nextInt(rooms.size()));
        Point mainArea = v.getMainArea();
        if (mainArea != null) {
            while (room.equals(mainArea)) {
                room = rooms.get(v.getRANDOM().nextInt(rooms.size()));
            }
        }
        return room;
    }
}
