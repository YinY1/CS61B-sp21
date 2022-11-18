package byow.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents temp variables during generating the world.
 *
 * @author Edward Tsang
 */
public class Variables {
    public final Random RANDOM;
    public ArrayList<Point> connections;
    public HashMap<Point, Point> roomAreas;
    public HashMap<Point, Point> root;
    public HashSet<Point> areas;
    public Point mainArea;

    Variables(long seed) {
        connections = new ArrayList<>();
        roomAreas = new HashMap<>();
        root = new HashMap<>();
        areas = new HashSet<>();
        mainArea = null;
        RANDOM = new Random(seed);
    }
}
