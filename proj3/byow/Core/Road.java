package byow.Core;

import byow.Core.World.Point;
import byow.TileEngine.Tileset;

import java.util.HashMap;

/**
 * Represents roads generation.
 *
 * @author Edward Tsang
 */
public class Road {
    public static void createRoad(World world) {
        HashMap<Point, Point> root = new HashMap<>();
        world.units.forEach(u -> root.put(u, u));
        findPath(world, root);
    }

    /**
     * generate random roads using Kruskal
     */
    private static void findPath(World world, HashMap<Point, Point> root) {
        while (!world.walls.isEmpty()) {
            int idx = world.getRANDOM().nextInt(world.walls.size());
            Point wall = world.walls.get(idx);
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
                    && !kruskalFind(unit1, root).equals(kruskalFind(unit2, root))) {

                kruskalUnion(unit1, unit2, root);
                root.put(wall, unit1);
                world.roads[x][y] = true;
                world.tiles[x][y] = Tileset.FLOOR;
            }
            world.walls.remove(idx);
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
}
