package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;
public class TestArrayDequeEC {

    @Test
    public void test1(){
        int N = 100000;
        StudentArrayDeque<Integer> a = new StudentArrayDeque<>();
        StudentArrayDeque<Integer> b = new StudentArrayDeque<>();
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
                b.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(a.size(),b.size());
            } else if (operationNumber == 2) {
                int size = a.size();
                if (size > 0) {
                    assertEquals(a.removeLast(),b.removeLast());
                }
            } else {
                int randVal = StdRandom.uniform(0, 100);
                a.addFirst(randVal);
                b.addFirst(randVal);
            }
        }
    }
}
