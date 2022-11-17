package byow.Core;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represent {x,y} point with a rank(depth in a tree)
 */
class Point {
    final int x;
    final int y;
    int rank;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
        rank = 0;
    }

    public static ArrayList<Point> getFourWaysPoints(Point p) {
        ArrayList<Point> ret = new ArrayList<>();
        final int[][] direction = new int[][]{{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
        for (int i = 0; i < 4; i++) {
            ret.add(new Point(p.x + direction[i][0], p.y + direction[i][1]));
        }
        return ret;
    }

    public static Point[] getNearPoint(World world, Point connection) {
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

    public static Point getCorrectPoint(World world, Point unit) {
        if (world.rooms[unit.x][unit.y]) {
            unit = Room.getBottomLeft(world, unit);
        }
        return unit;
    }

    public static boolean isNearMain(World world, Point point) {
        for (Point near : getFourWaysPoints(point)) {
            if (isInMainArea(world, near)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInMainArea(World world, Point unit) {
        return Objects.equals(world.root.get(unit), world.mainArea)
                || Objects.equals(world.root.get(world.root.get(unit)), world.mainArea);
    }

    @Override
    public boolean equals(Object p) {
        if (this == p) {
            return true;
        }
        if (p == null || p.getClass() != this.getClass()) {
            return false;
        }
        return p.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return x * 114514 + y;
    }
}
