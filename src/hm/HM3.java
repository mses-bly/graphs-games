package hm;

import graph.Adopters;
import graph.BiPartiteOps;
import graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by moises on 11/23/16.
 */
public class HM3 {
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

	public static void main(String[] args) throws FileNotFoundException {
		HM3 hm3 = new HM3();
		Graph g = hm3.constructWithCapacity(System.getProperty("user.dir") + "/src/hm/bipartite_1.txt");
		g.print();

		BiPartiteOps.maxMatching(g);
	}

}
