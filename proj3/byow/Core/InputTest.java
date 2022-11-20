package byow.Core;

import byow.TileEngine.TETile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputTest {
    public static void main(String[] args) {
        new InputTest().diff();
    }
    @Test
    public void same() {
        sameInput("n5197880843569031643swwwaasssssdd:q");
    }

    @Test
    public void diff() {
        diffInput();
    }

    @Test
    public void split(){
        Engine e = new Engine();
        assertEquals("N114SAAD:Q", Utils.fixInputString(e, "abc n114aa saad:qaad"));
        assertEquals("LAASAAD:Q", Utils.fixInputString(e, "abc Ln114aa saad:qaad"));
        //assertEquals("WASD",e.fixInputString("WASD"));
    }

    private void sameInput(String input) {
        Engine e = new Engine();
        TETile[][] a = e.interactWithInputString(input);
        TETile[][] b = e.interactWithInputString(input);
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[x].length; y++) {
                String msg = "(x,y):" + "(" + x + "," + y + ")";
                assertEquals(msg, a[x][y].description(), b[x][y].description());
            }
        }
    }

    private void diffInput() {
        Engine e = new Engine();
        TETile[][] a = e.interactWithInputString("n8004217737854698935s");
        TETile[][] b = e.interactWithInputString("n8272166368955537510s");
        TETile[][] c = e.interactWithInputString("n7341909481878015308s");
        TETile[][] d = e.interactWithInputString("n7922128623741339225s");
    }
}
