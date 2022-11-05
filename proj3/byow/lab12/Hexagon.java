package byow.lab12;

import org.junit.Test;

public class Hexagon {
    public static String[] addHexagon(int s) {
        String[] hex = new String[2 * s];
        for (int i = 0; i < s; i++) {
            String line = " ".repeat(s - i - 1) +
                    "a".repeat(s+2*i) +
                    " ".repeat(s - i - 1);
            hex[i] = line;
            hex[2 * s - i - 1] = line;
        }
        return hex;
    }

    @Test
    public void print() {
        for (int i = 1; i < 5; i++) {
            for (String l : addHexagon(i)) {
                System.out.println(l);
            }
        }
    }
}
