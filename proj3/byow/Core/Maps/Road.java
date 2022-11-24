package byow.Core.Maps;

import byow.Core.Point;
import byow.Core.Variables;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * Represents roads generation.
 *
 * @author Edward Tsang
 */
public class Road {
    public static void createRoad(World world, Variables v) {
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isRoad(x, y) || world.isWall(x, y)) {
                    Point p = new Point(x, y);
                    v.getRoot().put(p, p);
                }
            }
        }
        findPath(world, v);
    }

    /**
     * generate random roads using Kruskal
     */
    private static void findPath(World world, Variables v) {
        ArrayList<Point> walls = Wall.getAllWalls(world);
        while (!walls.isEmpty()) {
            int idx = v.getRANDOM().nextInt(walls.size());
            Point wall = walls.get(idx);
            // get the point near the wall
            int x = wall.getX();
            int y = wall.getY();
            int x1 = x % 2 == 1 ? x : x + 1;
            int x2 = x % 2 == 1 ? x : x - 1;
            int y1 = x % 2 == 1 ? y + 1 : y;
            int y2 = x % 2 == 1 ? y - 1 : y;

            Point unit1 = new Point(x1, y1);
            Point unit2 = new Point(x2, y2);

            var root = v.getRoot();
            // connect two roads if they don't intersect
            if (world.isRoad(x1, y1) && world.isRoad(x2, y2)
                    && !isIntersected(unit1, unit2, root)) {
                kruskalUnion(unit1, unit2, root);
                root.put(wall, unit1);
                world.getTiles()[x][y] = Tileset.FLOOR;
            }
            walls.remove(idx);
        }
    }

    /**
     * find ancestor and path compression
     */
    private static Point kruskalFind(Point unit, HashMap<Point, Point> root) {
        if (root.get(unit) != unit) {
            root.put(unit, kruskalFind(root.get(unit), root));
        }
        return root.get(unit);
    }

    /**
     * union two sets
     */
    private static void kruskalUnion(Point unit1, Point unit2, HashMap<Point, Point> root) {
        Point root1 = kruskalFind(unit1, root);
        Point root2 = kruskalFind(unit2, root);
        if (unit1.getRank() <= root2.getRank()) {
            root.put(root1, root2);
        } else {
            root.put(root2, root1);
        }
        if (unit1.getRank() == root2.getRank() && !root1.equals(root2)) {
            root2.addRank();
        }
    }

    private static boolean isIntersected(Point p1, Point p2, HashMap<Point, Point> root) {
        return kruskalFind(p1, root).equals(kruskalFind(p2, root));
    }

    public static void addRoadsToArea(World world, Variables v) {
        int w = world.getWidth();
        int h = world.getHeight();
        boolean[][] path = new boolean[w][h];

        var areas = v.getAreas();
        var root = v.getRoot();

        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                if (world.isRoad(x, y) && !path[x][y]) {
                    Point p = new Point(x, y);
                    path[x][y] = true;
                    areas.add(p);
                    root.put(p, p);

                    Point rootP = p;
                    Queue<Point> queue = new ArrayDeque<>();
                    queue.add(p);
                    while (!queue.isEmpty()) {
                        p = queue.poll();

                        for (Point near : Point.getFourWaysPoints(p)) {
                            int nearX = near.getX();
                            int nearY = near.getY();
                            if (world.isRoad(nearX, nearY) && !path[nearX][nearY]) {
                                queue.add(near);
                                root.put(near, rootP);
                                path[nearX][nearY] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void removeDeadEnds(World world) {
        boolean done = false;
        while (!done) {
            done = true;
            for (int x = 1; x < world.getWidth() - 1; x++) {
                for (int y = 1; y < world.getHeight() - 1; y++) {
                    if (world.isRoad(x, y) && isDeadEnd(world, x, y)) {
                        world.getTiles()[x][y] = Tileset.NOTHING;
                        done = false;
                    }
                }
            }
        }
    }

    private static boolean isDeadEnd(World world, int x, int y) {
        int count = 0;
        for (Point p : Point.getFourWaysPoints(x, y)) {
            if (world.getTiles()[p.getX()][p.getY()] == Tileset.WALL
                    || world.isNothing(p.getX(), p.getY())) {
                count++;
            }
        }
        return count == 3;
    }
}
