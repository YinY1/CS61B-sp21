package byow.Core;

import byow.Core.Maps.Room;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represent {x,y} point with a rank(depth in a tree)
 *
 * @author Edward Tsang
 */
public class Point {
    public final int x;
    public final int y;
    public int rank;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        rank = 0;
    }

    public static ArrayList<Point> getFourWaysPoints(Point p) {
        return getFourWaysPoints(p.x, p.y);
    }

    public static ArrayList<Point> getFourWaysPoints(int x, int y) {
        return new ArrayList<>(getEightWaysPoints(x, y).subList(0, 4));
    }

    public static ArrayList<Point> getEightWaysPoints(int x, int y) {
        ArrayList<Point> ret = new ArrayList<>();
        final int[][] direction = new int[][]{{1, 0}, {0, -1}, {-1, 0}, {0, 1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        for (int i = 0; i < 8; i++) {
            ret.add(new Point(x + direction[i][0], y + direction[i][1]));
        }
        return ret;
    }

    public static Point[] getNearPoint(World world, Point connection) {
        int x1 = connection.x + 1;
        int y1 = connection.y;
        int x2 = connection.x - 1;
        int y2 = connection.y;
        if (!world.isRoad(x1, y1) && !world.isRoom(x1, y1)) {
            x1 = connection.x;
            y1 = connection.y + 1;
            x2 = connection.x;
            y2 = connection.y - 1;
        }
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }

    public Point getCorrectPoint(World world) {
        if (world.isRoom(x, y)) {
            return Room.getBottomLeft(world, this);
        }
        return this;
    }

    public boolean isNearMain(World world, Variables v) {
        for (Point near : getFourWaysPoints(this)) {
            if (near.getCorrectPoint(world).isInMainArea(v)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInMainArea(Variables v) {
        return Objects.equals(v.root.get(this), v.mainArea)
                || Objects.equals(v.root.get(v.root.get(this)), v.mainArea);
    }

    @Override
    public boolean equals(Object p) {
        if (this == p) {
            return true;
        }
        if (p == null || p.getClass() != this.getClass()) {
            return false;
        }
        return ((Point) p).x == this.x && ((Point) p).y == this.y;
    }

    @Override
    public int hashCode() {
        return x * 114514 + y;
    }
}
