package byow.Core;

import byow.Core.Character.Characters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents temp variables during generating the world.
 *
 * @author Edward Tsang
 */
public class Variables implements Serializable {
    public Random RANDOM;
    public ArrayList<Point> connections;
    public HashMap<Point, Point> roomAreas;
    public HashMap<Point, Point> root;
    public HashSet<Point> areas;
    public Point mainArea;
    World world;
    World tempWorld;
    Characters characters;

    Variables(long seed) {
        connections = new ArrayList<>();
        roomAreas = new HashMap<>();
        root = new HashMap<>();
        areas = new HashSet<>();
        mainArea = null;
        RANDOM = new Random(seed);
    }

    Variables() {
        world = new World(Engine.WIDTH - 3, Engine.HEIGHT - 3);
    }
}
