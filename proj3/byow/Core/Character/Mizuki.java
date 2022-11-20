package byow.Core.Character;

import byow.Core.World;

import java.io.Serializable;

public class Mizuki implements Serializable {
    private int x;
    private int y;
    private int health;
    //TODO


    public Mizuki(int x, int y) {
        health = 100;
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void goUp(World world) {
        if (world.isUnit(x, y + 1)) {
            this.y++;
        }
    }

    public void goDown(World world) {

        if (world.isUnit(x, y - 1)) {
            this.y--;
        }
    }

    public void goLeft(World world) {
        if (world.isUnit(x-1, y)) {
            this.x--;
        }
    }

    public void goRight(World world) {

        if (world.isUnit(x+1, y)) {
            this.x++;
        }
    }
}
