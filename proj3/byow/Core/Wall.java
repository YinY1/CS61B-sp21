package byow.Core;

import byow.Core.World.Point;
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
                        world.walls.add(new World.Point(x, y));
                        //world.tiles[x][y] = Tileset.WALL;
                    }
                    world.units.add(new World.Point(x, y));
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

    public static void connect(World world) {
        Room.addRoomsToRoot(world);
        Point mainArea = null;
        // get a random room as main area
        ArrayList<Point> keys = new ArrayList<>(world.roomAreas.keySet());
        int idx = world.getRANDOM().nextInt(keys.size());
        Point bottomLeft = keys.get(idx);
        Point topRight = world.roomAreas.get(bottomLeft);
        Point connection = Room.getRandomConnectionPoint(world, bottomLeft, topRight);
        world.tiles[connection.x][connection.y] = Tileset.UNLOCKED_DOOR;

        Point[] nears = getNearPoint(world, connection);
        Point unit1 = nears[0];
        Point unit2 = nears[1];
        world.tiles[unit1.x][unit1.y] = Tileset.FLOWER;
        world.tiles[unit2.x][unit2.y] = Tileset.FLOWER;

        connect(world, unit1, unit2, mainArea);


    }

    private static void connect(World world, Point unit1, Point unit2, Point mainArea) {
        if (Room.isRoom(world.rooms, unit1)) {
            unit1 = Room.getBottomLeft(world, unit1);
            if (mainArea == null) {
                mainArea = unit1;
            }
        }
        if (Room.isRoom(world.rooms, unit2)) {
            unit2 = Room.getBottomLeft(world, unit2);
            if (mainArea == null) {
                mainArea = unit2;
            }
        }
        Road.kruskalUnion(unit1, unit2, world.root);
        removeRestConnection(world);
    }

    private static Point[] getNearPoint(World world, Point connection) {
        int x1 = connection.x + 1;
        int y1 = connection.y;
        int x2 = connection.x - 1;
        int y2 = connection.y;
        if (!world.roads[x1][y1] && !world.rooms[x1][y1]) {
            x1 = connection.x;
            y1 = connection.y + 1;
            x2 = connection.x;
            y2 = connection.y - 1;
        }
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }

    private static void removeRestConnection(World world) {
        ArrayList<Point> connections = new ArrayList<>(world.connections);
        for (Point c : connections) {
            Point[] nears = getNearPoint(world, c);
            Point u1 = getCorrectPoint(world, nears[0]);
            Point u2 = getCorrectPoint(world, nears[1]);
            if (Road.isIntersected(u1, u2, world.root)) {
                if(world.tiles[c.x][c.y] != Tileset.UNLOCKED_DOOR) {
                    world.tiles[c.x][c.y] = Tileset.WALL;
                }
                world.connections.remove(c);
            }
        }
    }

    private static Point getCorrectPoint(World world, Point unit) {
        if (Room.isRoom(world.rooms, unit)) {
            unit = Room.getBottomLeft(world, unit);
        }
        return unit;
    }

    private static boolean isInMainArea(World world, Point unit, Point mainArea) {
        return mainArea.equals(Road.kruskalFind(unit, world.root));
    }
}
