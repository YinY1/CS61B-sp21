package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.print("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        System.out.println("Timing table for addLast");
        AList<Integer> Ns = new AList<>();
        int x = 1000;
        for (int i = 1; i <= 8; i+=1) {
            Ns.addLast(x);
            x*=2;
        }
        AList<Double> times = new AList<>();
        for (int i = 0; i < Ns.size(); i += 1) {
            int n = Ns.get(i);
            AList<Integer> a = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < n; j += 1) {
                a.addLast(114514);
            }
            double time = sw.elapsedTime();
            times.addLast(time);
        }
        printTimingTable(Ns, times, Ns);
    }
}
