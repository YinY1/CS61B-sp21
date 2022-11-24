package byow.Core.Maps;

import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.util.ArrayList;

/**
 * Represents walls generation and rebuild.
 *
 * @author Edward Tsang
 */
public class Wall {

    public static void createWalls(World world) {
        int width = world.getWidth();
        int height = world.getHeight();
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (world.isNothing(x, y)) {
                    if (x % 2 == 1 && y % 2 == 1) {
                        world.getTiles()[x][y] = Tileset.FLOOR;
                    } else {
                        world.getTiles()[x][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public static void findConnection(World world, Variables v) {
        var connections = v.getConnections();
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

    public static void connectAreas(World world, Variables v) {
        ArrayList<Point> connections = v.getConnections();
        while (!connections.isEmpty()) {
            Point connection = getRandomConnection(world, v);
            connect(world, connection, v);
            removeRestConnection(world, v);
        }
    }

    private static void removeRestConnection(World world, Variables v) {
        var connections = v.getConnections();
        for (Point c : new ArrayList<>(connections)) {
            var nears = Point.getNearPoint(world, c);
            Point u1 = nears[0].getCorrectPoint(world);
            Point u2 = nears[1].getCorrectPoint(world);
            int x = c.getX();
            int y = c.getY();
            if (u1.isInMainArea(v) && u2.isInMainArea(v)) {
                if (!world.isDoor(x, y)) {
                    if (v.getRANDOM().nextInt(v.getRoomAreas().size()) < 1) {
                        world.getTiles()[x][y] = Tileset.UNLOCKED_DOOR;
                    } else {
                        world.getTiles()[x][y] = Tileset.WALL;
                    }
                    connections.remove(c);
                }
            }
        }
    }

    /**
     * connect two area, remain the main area, remove the other one
     */
    private static void connect(World world, Point connection, Variables v) {
        var near = Point.getNearPoint(world, connection);
        Point u1 = near[0];
        Point u2 = near[1];
        world.getTiles()[connection.getX()][connection.getY()] = Tileset.UNLOCKED_DOOR;
        v.getConnections().remove(connection);
        u1 = u1.getCorrectPoint(world);
        u2 = u2.getCorrectPoint(world);
        var root = v.getRoot();
        if (!u1.isInMainArea(v)) {
            v.getAreas().remove(root.get(u1));
            root.put(root.get(u1), v.getMainArea());
        } else {
            v.getAreas().remove(root.get(u2));
            root.put(root.get(u2), v.getMainArea());
        }
    }

    private static Point getRandomConnection(World world, Variables v) {
        Point ret = null;
        var connections = v.getConnections();
        while ((ret == null || !ret.isNearMain(world, v))) {
            ret = connections.get(v.getRANDOM().nextInt(connections.size()));
        }
        return ret;
    }

    public static void buildWallNearUnit(World world) {
        getAllWalls(world).forEach(w -> world.getTiles()[w.getX()][w.getY()] = Tileset.NOTHING);
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isUnit(x, y)) {
                    for (Point p : Point.getEightWaysPoints(x, y)) {
                        int pX = p.getX();
                        int pY = p.getY();
                        if (!world.isUnit(pX, pY)) {
                            world.getTiles()[pX][pY] = Tileset.WALL;
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

    public static void creatEntryAndExit(World world, Variables v) {
        Point mainArea = v.getMainArea();
        world.getTiles()[mainArea.getX()][mainArea.getY()] = Tileset.ENTRY;
        world.setEntry(new Point(mainArea.getX(), mainArea.getY()));
        Point room = Room.getRandomRoom(v);
        Point topRight = v.getRoomAreas().get(room);
        int x = room.getX() + 1;
        int y = room.getY() + 1;
        for (; x < topRight.getX(); x++) {
            for (; y < topRight.getY(); y++) {
                if (v.getRANDOM().nextInt(5) == 0) {
                    world.getTiles()[x][y] = Tileset.LOCKED_DOOR;
                    return;
                }
            }
        }
        world.getTiles()[x][y] = Tileset.LOCKED_DOOR;
        world.setExit(new Point(x, y));
    }
}
