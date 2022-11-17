package byow.Core;

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
    public static void createRoad(World world) {
        world.units.forEach(u -> world.root.put(u, u));
        findPath(world, world.root);
    }

    /**
     * generate random roads using Kruskal
     */
    private static void findPath(World world, HashMap<Point, Point> root) {
        ArrayList<Point> walls = new ArrayList<>(world.walls);
        while (!walls.isEmpty()) {
            int idx = world.getRANDOM().nextInt(walls.size());
            Point wall = walls.get(idx);
            // get the point near the wall
            int x = wall.x;
            int y = wall.y;
            int x1 = x % 2 == 1 ? x : x + 1;
            int x2 = x % 2 == 1 ? x : x - 1;
            int y1 = x % 2 == 1 ? y + 1 : y;
            int y2 = x % 2 == 1 ? y - 1 : y;

            Point unit1 = new Point(x1, y1);
            Point unit2 = new Point(x2, y2);

            // connect two roads if they don't intersect
            if (world.roads[x1][y1] && world.roads[x2][y2]
                    && !isIntersected(unit1, unit2, root)) {
                kruskalUnion(unit1, unit2, root);
                root.put(wall, unit1);
                world.roads[x][y] = true;
                world.tiles[x][y] = Tileset.ROAD;
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
        if (unit1.rank <= root2.rank) {
            root.put(root1, root2);
        } else {
            root.put(root2, root1);
        }
        if (unit1.rank == root2.rank && !root1.equals(root2)) {
            root2.rank++;
        }
    }

    private static boolean isIntersected(Point p1, Point p2, HashMap<Point, Point> root) {
        return kruskalFind(p1, root).equals(kruskalFind(p2, root));
    }

    public static void addRoadsToArea(World world) {
        int w = world.getWidth();
        int h = world.getHeight();
        boolean[][] path = new boolean[w][h];
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                if (world.roads[x][y] && !path[x][y]) {
                    Point p = new Point(x, y);
                    path[x][y] = true;
                    world.areas.add(p);
                    world.root.put(p, p);

                    Point rootP = p;
                    Queue<Point> queue = new ArrayDeque<>();
                    queue.add(p);
                    while (!queue.isEmpty()) {
                        p = queue.poll();

                        for (Point near : Point.getFourWaysPoints(p)) {
                            if (world.roads[near.x][near.y] && !path[near.x][near.y]) {
                                queue.add(near);
                                world.root.put(near, rootP);
                                path[near.x][near.y] = true;
                            }
                        }
                    }
                }
            }
        }
    }
}
