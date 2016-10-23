package hm;

import graph.Graph;
import graph.GraphOps;

import java.io.File;
import java.io.FileNotFoundException;
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

	public static void main(String[] args) throws FileNotFoundException {
		HM2 hm2 = new HM2();
		Graph g = hm2.construct(System.getProperty("user.dir") + "/src/hm/test.txt", true);

		System.out.println(GraphOps.contagion(g, 2, 0.5).size());

//		System.out.println(g.size());
//		g.print();
//		long t1 = System.currentTimeMillis();
//		System.out.println(run10C(g,1000));
//		long t2 = System.currentTimeMillis();
//		System.out.println((t2 - t1) / (double) (60 * 1000));


//		System.out.println(GraphOps.maxFlow(g, 0, 4038));
//		System.out.println(GraphOps.maxFlow(g, 0, 4038));

//		g.print();
//
//		Graph copy = new Graph(g);
//
//		Node n = p.nodePath(g).get(1);
//		n.c = 10000;
//
//		g.print();
//
//		copy.print();
	}
}
