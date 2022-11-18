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
                        world.tiles[x][y] = Tileset.FLOOR;
                    } else {
                        world.tiles[x][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public static void findConnection(World world, Variables v) {
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y) && ableToConnect(world, x, y)) {
                    v.connections.add(new Point(x, y));
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
        while (!v.connections.isEmpty()) {
            Point connection = getRandomConnection(world, v);
            connect(world, connection, v);
            removeRestConnection(world, v);
        }
    }

    private static void removeRestConnection(World world, Variables v) {
        for (Point c : new ArrayList<>(v.connections)) {
            Point[] nears = Point.getNearPoint(world, c);
            Point u1 = nears[0].getCorrectPoint(world);
            Point u2 = nears[1].getCorrectPoint(world);
            if (u1.isInMainArea(v) && u2.isInMainArea(v)) {
                if (!world.isDoor(c.x, c.y)) {
                    if (v.RANDOM.nextInt(v.roomAreas.size()) < 1) {
                        world.tiles[c.x][c.y] = Tileset.UNLOCKED_DOOR;
                    } else {
                        world.tiles[c.x][c.y] = Tileset.WALL;
                    }
                    v.connections.remove(c);
                }
            }
        }
    }

    /**
     * connect two area, remain the main area, remove the other one
     */
    private static void connect(World world, Point connection, Variables v) {
        Point[] near = Point.getNearPoint(world, connection);
        Point u1 = near[0];
        Point u2 = near[1];
        world.tiles[connection.x][connection.y] = Tileset.UNLOCKED_DOOR;
        v.connections.remove(connection);
        u1 = u1.getCorrectPoint(world);
        u2 = u2.getCorrectPoint(world);
        if (!u1.isInMainArea(v)) {
            v.areas.remove(v.root.get(u1));
            v.root.put(v.root.get(u1), v.mainArea);
        } else {
            v.areas.remove(v.root.get(u2));
            v.root.put(v.root.get(u2), v.mainArea);
        }
    }

    private static Point getRandomConnection(World world, Variables v) {
        Point ret = null;
        while ((ret == null || !ret.isNearMain(world, v))) {
            ret = v.connections.get(v.RANDOM.nextInt(v.connections.size()));
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

    public static void creatEntryAndExit(World world, Variables v){
        world.tiles[v.mainArea.x][v.mainArea.y] = Tileset.MIZUKI;
        Point room = Room.getRandomRoom(v);
        Point topRight = v.roomAreas.get(room);
        int x = room.x+1;
        int y = room.y+1;
        for (; x < topRight.x ; x++) {
            for (; y < topRight.y; y++) {
                if(v.RANDOM.nextInt(5)==0){
                    world.tiles[x][y] = Tileset.LOCKED_DOOR;
                    return;
                }
            }
        }
        world.tiles[x][y] = Tileset.LOCKED_DOOR;
    }
}
