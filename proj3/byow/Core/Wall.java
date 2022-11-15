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
                        //tiles[x][y] = Tileset.WALL;
                    }
                    world.units.add(new World.Point(x, y));
                }
            }
        }
    }

    public static void findConnection(World world) {
        for (Point w : world.walls) {
            if (isConnected(world, w.x, w.y)) {
                world.tiles[w.x][w.y] = Tileset.FLOOR;
                world.connections.add(new Point(w.x, w.y));
            }
        }
    }

    private static boolean isConnected(World world, int x, int y) {
        return world.roads[x - 1][y] && world.rooms[x + 1][y]
                || world.roads[x + 1][y] && world.rooms[x - 1][y]
                || world.roads[x][y - 1] && world.rooms[x][y + 1]
                || world.roads[x][y + 1] && world.rooms[x][y - 1]
                || world.rooms[x][y + 1] && world.rooms[x][y - 1]
                || world.rooms[x + 1][y] && world.rooms[x - 1][y];
    }

    public static void connect(World world) {
        // get a random room as main area
        ArrayList<Point> keys = new ArrayList<>(world.roomAreas.keySet());
        int idx = world.getRANDOM().nextInt(keys.size());
        Point bottomLeft = keys.get(idx);
        Point topRight = world.roomAreas.get(bottomLeft);
        Point connection = Room.getRandomConnectionPoint(world, bottomLeft, topRight);
    }

    private static void connect(World world, Point unit1, Point unit2) {

        if (Room.isRoom(world.rooms, unit1)) {

        }
    }

}
