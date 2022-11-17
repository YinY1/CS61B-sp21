package byow.Core;

import byow.TileEngine.Tileset;

import java.util.ArrayList;

/**
 * Represents walls generation and rebuild.
 *
 * @author Edward Tsang
 */
public class Wall {
    private static final ArrayList<Point> connections = new ArrayList<>();

    public static void createWalls(World world) {
        int width = world.getWidth();
        int height = world.getHeight();
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (world.isNothing(x, y)) {
                    if (x % 2 == 1 && y % 2 == 1) {
                        world.tiles[x][y] = Tileset.FLOOR;
                    } else {
                        world.tiles[x][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public static void findConnection(World world) {
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y) && ableToConnect(world, x, y)) {
                    connections.add(new Point(x, y));
                }
            }
        }
    }

    private static boolean ableToConnect(World world, int x, int y) {
        return world.isRoad(x - 1, y) && world.isRoom(x + 1, y)
                || world.isRoad(x + 1, y) && world.isRoom(x - 1, y)
                || world.isRoad(x, y - 1) && world.isRoom(x, y + 1)
                || world.isRoad(x, y + 1) && world.isRoom(x, y - 1)
                || world.isRoom(x, y + 1) && world.isRoom(x, y - 1)
                || world.isRoom(x + 1, y) && world.isRoom(x - 1, y);
    }

    public static void connectAreas(World world) {
        while (!connections.isEmpty()) {
            Point connection = getRandomConnection(world);
            connect(world, connection);
            removeRestConnection(world);
        }
    }

    private static void removeRestConnection(World world) {
        for (Point c : new ArrayList<>(connections)) {
            Point[] nears = Point.getNearPoint(world, c);
            Point u1 = Point.getCorrectPoint(world, nears[0]);
            Point u2 = Point.getCorrectPoint(world, nears[1]);
            if (Point.isInMainArea(world, u1) && Point.isInMainArea(world, u2)) {
                if (world.tiles[c.x][c.y] != Tileset.UNLOCKED_DOOR) {
                    if (world.getRANDOM().nextInt(Room.getRoomAreasNum()) < 1) {
                        world.tiles[c.x][c.y] = Tileset.UNLOCKED_DOOR;
                    } else {
                        world.tiles[c.x][c.y] = Tileset.WALL;
                    }
                    connections.remove(c);
                }
            }
        }
    }

    /**
     * connect two area, remain the main area, remove the other one
     */
    private static void connect(World world, Point connection) {
        Point[] near = Point.getNearPoint(world, connection);
        Point u1 = near[0];
        Point u2 = near[1];
        world.tiles[connection.x][connection.y] = Tileset.UNLOCKED_DOOR;
        connections.remove(connection);
        u1 = Point.getCorrectPoint(world, u1);
        u2 = Point.getCorrectPoint(world, u2);
        if (!Point.isInMainArea(world, u1)) {
            world.areas.remove(world.root.get(u1));
            world.root.put(world.root.get(u1), world.mainArea);
        } else {
            world.areas.remove(world.root.get(u2));
            world.root.put(world.root.get(u2), world.mainArea);
        }
    }

    private static Point getRandomConnection(World world) {
        Point ret = null;
        while (ret == null || !Point.isNearMain(world, ret)) {
            ret = connections.get(world.getRANDOM().nextInt(connections.size()));
        }
        return ret;
    }

    public static void buildWallNearUnit(World world) {
        getAllWalls(world).forEach(w -> world.tiles[w.x][w.y] = Tileset.NOTHING);
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isUnit(x, y)) {
                    for (Point p : Point.getEightWaysPoints(x, y)) {
                        if (!world.isUnit(p.x, p.y)) {
                            world.tiles[p.x][p.y] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<Point> getAllWalls(World world) {
        ArrayList<Point> ret = new ArrayList<>();
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y)) {
                    ret.add(new Point(x, y));
                }
            }
        }
        return ret;
    }
}
