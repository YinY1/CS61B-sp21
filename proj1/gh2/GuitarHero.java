package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    private static GuitarString[] strings;

    public static double concert(int index) {
        return 440 * Math.pow(2, (index - 24) / 12.0);
    }

    private static void initialize() {
        int SIZE = 37;
        strings = new GuitarString[SIZE];
        for (int i = 0; i < SIZE; i++) {
            strings[i] = new GuitarString(concert(i));
        }
    }

    public static void main(String[] args) {
        initialize();
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index >= 0) {
                    strings[index].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (GuitarString elem : strings) {
                sample += elem.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString elem : strings) {
                elem.tic();
            }
        }
    }


}

