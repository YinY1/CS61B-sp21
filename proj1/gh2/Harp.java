package gh2;

import deque.Deque;

public class Harp extends GuitarString {


    public Harp(double frequency) {
        super(frequency*2);
    }

    @Override
    public void tic() {
        double first = this.buffer.removeFirst();
        double second = this.buffer.get(0);
        double newSample = DECAY * 0.5 * (first + second);
        this.buffer.addLast(-newSample);
    }
}
