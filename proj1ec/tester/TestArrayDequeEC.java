package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void testFailed() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
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
                if(size > 0){
                    if(randVal <50){
                        msg+="removeFirst()\n";
                        assertEquals(msg,solution.removeFirst(),student.removeFirst());
                    } else {
                        msg +="removeLast()\n";
                        assertEquals(msg,solution.removeLast(),student.removeLast());
                    }
                }
            }
        }
    }
}
