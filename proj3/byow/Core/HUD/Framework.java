package byow.Core.HUD;

import byow.Core.Engine;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Framework {
    public int depth;
    public int health;

    public Framework(){
        depth = 1;
        health = 100;
    }

    public void drawMenu(){
        final double w = Engine.WIDTH/2.0;
        final double h  = Engine.HEIGHT/2.0;
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, Engine.WIDTH);
        StdDraw.setYscale(0, Engine.HEIGHT);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(w,h,"Mizuki");

        font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.text(w,h-5,"NEW GAME(N)");
        StdDraw.show();
    }

    public void drawFramework(){
        drawDepth();
    }

    private void drawDepth(){
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(4,1," ---  Depth: "+depth+"  --- ");
        StdDraw.show();
    }
}
