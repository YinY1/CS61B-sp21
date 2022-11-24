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
    World world;
    World tempWorld;
    Characters characters;
    private Random RANDOM;
    private ArrayList<Point> connections;
    private HashMap<Point, Point> roomAreas;
    private HashMap<Point, Point> root;
    private HashSet<Point> areas;
    private Point mainArea;

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

    public Random getRANDOM() {
        return RANDOM;
    }

    public ArrayList<Point> getConnections() {
        return connections;
    }

    public Characters getCharacters() {
        return characters;
    }

    public HashMap<Point, Point> getRoomAreas() {
        return roomAreas;
    }

    public HashMap<Point, Point> getRoot() {
        return root;
    }

    public HashSet<Point> getAreas() {
        return areas;
    }

    public Point getMainArea() {
        return mainArea;
    }

    public void setMainArea(Point mainArea) {
        this.mainArea = mainArea;
    }

    public World getTempWorld() {
        return tempWorld;
    }

    public World getWorld() {
        return world;
    }
}
