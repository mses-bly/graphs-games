package hm;

import graph.Graph;
import graph.GraphOps;
import graph.Path;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by moises on 10/20/16.
 */
public class HM2 {
	private Scanner sc;

	private Graph construct(String filename) throws FileNotFoundException {
		sc = new Scanner(new File(filename));
		int N = sc.nextInt();
		Graph g = new Graph(N);
		while (sc.hasNext()) {
			int s = sc.nextInt();
			int t = sc.nextInt();
			g.connect(s, t, 1, 1);
		}
		return g;
	}

	public static void main(String[] args) throws FileNotFoundException {
		HM2 hm2 = new HM2();
		Graph g = hm2.construct(System.getProperty("user.dir") + "/src/hm/test.txt");


		System.out.println(GraphOps.maxFlow(g, 0, 3));

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
