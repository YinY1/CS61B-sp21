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
    final Random RANDOM;
    ArrayList<Point> connections;
    HashMap<Point, Point> roomAreas;
    HashMap<Point, Point> root;
    HashSet<Point> areas;
    Point mainArea;

    Variables(long seed) {
        connections = new ArrayList<>();
        roomAreas = new HashMap<>();
        root = new HashMap<>();
        areas = new HashSet<>();
        mainArea = null;
        RANDOM = new Random(seed);
    }
}
