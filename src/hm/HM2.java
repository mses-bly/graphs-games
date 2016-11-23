package hm;

import graph.Adopters;
import graph.Graph;
import graph.GraphOps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by moises on 10/20/16.
 */
public class HM2 {
	private Scanner sc;

	private Graph constructWithCapacity(String filename) throws FileNotFoundException {
		sc = new Scanner(new File(filename));
		int N = sc.nextInt();
		Graph g = new Graph(N);
		while (sc.hasNext()) {
			int s = sc.nextInt();
			int t = sc.nextInt();
			int c = sc.nextInt();
			g.connect(s, t, 1, c);
		}
		return g;
	}


	private Graph construct(String filename, boolean undirected) throws FileNotFoundException {
		sc = new Scanner(new File(filename));
		int N = sc.nextInt();
		Graph g = new Graph(N);
		while (sc.hasNext()) {
			int s = sc.nextInt();
			int t = sc.nextInt();
			if (undirected)
				g.connectUnd(s, t, 1, 1);
			else
				g.connect(s, t, 1, 1);
		}
		return g;
	}

	public static double run10C(Graph g, int times) {
		double avg = 0;
		for (int k = 0; k < times; k++) {
			Random r = new Random(System.currentTimeMillis());
			int i = r.nextInt(g.size());
			int j = r.nextInt(g.size());
			double res = GraphOps.maxFlow(g, i, j);
//			System.out.println(i + ", " + j);
			avg += res;
		}
		return avg / times;
	}

	public static void run12b(Graph g) {
		int infected = 0;
		for (int i = 0; i < 100; i++) {
			infected += GraphOps.contagion(g, 10, 0.1).size();
		}
		System.out.println((double) infected / 100);
	}

//	Run your algorithm several (10) times with different values of q (try increments
//	of 0.05 from 0 to 0.5), and with different values of k (try increments of 10 from
//			0 to 250). Observe and record the rates of “infection” under various conditions.
//	What conditions on k and q are likely to produce a complete cascade in this
//	particular graph, given your observations?

	public static void run12c(Graph g) {
		double q = 0;
		while (q <= 1) {
			int k = 0;
			while (k <= 500) {
				System.out.println(k + " , " + q + " , " + GraphOps.contagion(g, k, q).size());
				k += 10;
			}
			q += 0.05;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		HM2 hm2 = new HM2();
		Graph g = hm2.construct(System.getProperty("user.dir") + "/src/hm/facebook_combined.txt", true);
//		g.print();
		Adopters A = new Adopters(g, 0.3);
		System.out.println(A.earlyAdopters().size());

//		HashSet<Integer> S = new HashSet<>();
//		S.add(1);
//		S.add(0);
//
//		int[] s = {1, 0};
//		System.out.println(GraphOps.contagion(g, s, 0.5));

	}
}
