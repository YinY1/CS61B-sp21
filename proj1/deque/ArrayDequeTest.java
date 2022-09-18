package deque;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayDeque;

public class ArrayDequeTest {
    @Test
    public void testRandom() {
        deque.ArrayDeque<Integer> student = new deque.ArrayDeque<>();
        ArrayDeque<Integer> solution = new ArrayDeque<>();
        String msg = "";
        for (int i = 0; i < StdRandom.uniform(0, 1000000); i++) {
            double choice = StdRandom.uniform();
            Integer randVal = StdRandom.uniform(0, 100);
            if (choice < 0.33) {
                student.addLast(randVal);
                solution.addLast(randVal);
                msg += "addLast(" + randVal + ")\n";
            } else if (choice < 0.67) {
                student.addFirst(randVal);
                solution.addFirst(randVal);
                msg += "addFirst(" + randVal + ")\n";
            } else {
                int size = student.size();
                msg += "size()\n";
                if (size > 0) {
                    if (randVal < 50) {
                        msg += "removeFirst()\n";
                        assertEquals(msg, solution.removeFirst(), student.removeFirst());
                    } else {
                        msg += "removeLast()\n";
                        assertEquals(msg, solution.removeLast(), student.removeLast());
                    }
                }
            }
        }
    }

    @Test
    public void testReSize() {
        deque.ArrayDeque<Integer> student = new deque.ArrayDeque<>();
        ArrayDeque<Integer> solution = new ArrayDeque<>();
        student.addFirst(1);
        solution.addFirst(1);
        student.addFirst(2);
        solution.addFirst(2);
        assertEquals(solution.removeFirst(), student.removeFirst());
        assertEquals(solution.removeFirst(), student.removeFirst());
    }

    @Test
    public void testReSize2() {
        deque.ArrayDeque<Integer> student = new deque.ArrayDeque<>();
        ArrayDeque<Integer> solution = new ArrayDeque<>();
        student.addLast(0);
        student.addLast(1);
        solution.addLast(0);
        solution.addLast(1);
        assertEquals(solution.removeFirst(), student.removeFirst());
        solution.addLast(3);
        solution.addLast(4);
        solution.addLast(5);
        solution.addLast(6);
        solution.addLast(7);
        solution.addLast(8);
        student.addLast(3);
        student.addLast(4);
        student.addLast(5);
        student.addLast(6);
        student.addLast(7);
        student.addLast(8);
        assertEquals(solution.removeFirst(), student.removeFirst());
    }
}
