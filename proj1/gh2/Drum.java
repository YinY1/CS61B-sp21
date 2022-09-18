package gh2;

import edu.princeton.cs.algs4.StdRandom;

public class Drum extends GuitarString {

    static final double DECAY = 1.0;

    public Drum(double frequency) {
        super(frequency/1.5);
    }

    @Override
    public void tic() {
        double first = this.buffer.removeFirst();
        double second = this.buffer.get(0);
        double newSample = DECAY * 0.5 * (first + second);
        double probability = StdRandom.uniform();
        if (probability < 0.5) {
            this.buffer.addLast(newSample);
        } else {
            this.buffer.addLast(-newSample);
        }
    }
}
