package byow.Core;

import byow.TileEngine.Tileset;

import java.util.ArrayList;

public class Wall {

    public static void createWalls(World world) {
        int width = world.getWidth();
        int height = world.getHeight();
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (x * y == 0 || x == width - 1 || y == height - 1) {
                    world.tiles[x][y] = Tileset.WALL;
                } else if (world.isNothing(x, y)) {
                    if (x % 2 == 1 && y % 2 == 1) {
                        world.roads[x][y] = true;
                        world.tiles[x][y] = Tileset.ROAD;
                    } else {
                        world.walls.add(new Point(x, y));
                    }
                    world.units.add(new Point(x, y));
                }
            }
        }
    }

    public static void findConnection(World world) {
        for (Point w : world.walls) {
            if (ableToConnect(world, w.x, w.y)) {
                world.tiles[w.x][w.y] = Tileset.FLOOR;
                world.connections.add(new Point(w.x, w.y));
            }
        }
    }

    private static boolean ableToConnect(World world, int x, int y) {
        return world.roads[x - 1][y] && world.rooms[x + 1][y]
                || world.roads[x + 1][y] && world.rooms[x - 1][y]
                || world.roads[x][y - 1] && world.rooms[x][y + 1]
                || world.roads[x][y + 1] && world.rooms[x][y - 1]
                || world.rooms[x][y + 1] && world.rooms[x][y - 1]
                || world.rooms[x + 1][y] && world.rooms[x - 1][y];
    }

    public static void connectAreas(World world) {
        while (!world.connections.isEmpty()) {
            Point connection = getRandomConnection(world);
            connect(world, connection);
            removeRestConnection(world);
        }
    }

    private static void removeRestConnection(World world) {
        ArrayList<Point> connections = new ArrayList<>(world.connections);
        for (Point c : connections) {
            Point[] nears = Point.getNearPoint(world, c);
            Point u1 = Point.getCorrectPoint(world, nears[0]);
            Point u2 = Point.getCorrectPoint(world, nears[1]);
            if (Point.isInMainArea(world, u1) && Point.isInMainArea(world, u2)) {
                if (world.tiles[c.x][c.y] != Tileset.UNLOCKED_DOOR) {
                    world.tiles[c.x][c.y] = Tileset.WALL;
                    world.connections.remove(c);
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
        world.tiles[u1.x][u1.y] = Tileset.FLOWER;
        world.tiles[u2.x][u2.y] = Tileset.FLOWER;
        world.tiles[connection.x][connection.y] = Tileset.UNLOCKED_DOOR;
        world.connections.remove(connection);
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
            ret = world.connections.get(world.getRANDOM().nextInt(world.connections.size()));
        }
        return ret;
    }
}
