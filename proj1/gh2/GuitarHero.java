package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    private static GuitarString[] strings;
    private static Harp[] harps;
    private static Drum[] drums;

    private static final int SIZE = 37;

    public static double concert(int index) {
        return 440 * Math.pow(2, (index - 24) / 12.0);
    }

    private static void string() {
        strings = new GuitarString[SIZE];
        for (int i = 0; i < SIZE; i++) {
            strings[i] = new GuitarString(concert(i));
        }
    }

    private static void harp() {
        harps = new Harp[SIZE];
        for (int i = 0; i < SIZE; i++) {
            harps[i] = new Harp(concert(i));
        }
    }

    private static void drum() {
        drums = new Drum[SIZE];
        for (int i = 0; i < SIZE; i++) {
            drums[i] = new Drum(concert(i));
        }
    }

    public static void main(String[] args) {
        string();
        harp();
        drum();
        while (true) {

            /* check if the user has typed a key; if so, process it */
            play(strings);
            //play(harps);
            //play(drums);
        }
    }

    private static void play(GuitarString[] t) {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            int index = keyboard.indexOf(key);
            if (index >= 0) {
                t[index].pluck();
            }
        }

        /* compute the superposition of samples */
        double sample = 0;
        for (GuitarString elem : t) {
            sample += elem.sample();
        }

        /* play the sample on standard audio */
        StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
        for (GuitarString elem : t) {
            elem.tic();
        }
    }


}

