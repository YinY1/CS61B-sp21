package byow.Core.Character;

import byow.Core.Point;
import byow.Core.World;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;

public class Characters implements Serializable {
    private final Mizuki mizuki;
    private final ArrayList<Point> npc = new ArrayList<>();

    public Characters(World world) {
        Point entry = world.getEntry();
        mizuki = new Mizuki(entry.getX(), entry.getY());
    }

    public void setCharacters(World world, String input) {
        switch (input) {
            case "w", "W" -> mizuki.goUp(world);
            case "s", "S" -> mizuki.goDown(world);
            case "a", "A" -> mizuki.goLeft(world);
            case "d", "D" -> mizuki.goRight(world);
        }
        world.getTiles()[mizuki.getX()][mizuki.getY()] = Tileset.MIZUKI;
    }
}
