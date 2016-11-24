package hm;

import graph.BiPartiteOps;
import graph.Graph;
import graph.Market;
import graph.Matching;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by moises on 11/23/16.
 */
public class HM3 {
	private Scanner sc;


	private Market constructMarket(String filename) throws FileNotFoundException {
		sc = new Scanner(new File(filename));
		int N = sc.nextInt();
		Graph g = new Graph(N);
		HashMap<Integer, Double> sellerPrices = new HashMap<>();
		// Number of sellers prices to read.
		int S = sc.nextInt();
		while (S > 0) {
			int s = sc.nextInt();
			double p = sc.nextDouble();
			sellerPrices.put(s, p);
			S--;
		}
		while (sc.hasNext()) {
			int s = sc.nextInt();
			int t = sc.nextInt();
			int c = sc.nextInt();
			g.connect(s, t, 1, c);
		}
		Market market = new Market(g, sellerPrices);
		return market;
	}

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
//		Graph g = hm3.constructWithCapacity(System.getProperty("user.dir") + "/src/hm/bipartite_1.txt");
//		g.print();
//
//		Matching m = BiPartiteOps.maxMatching(g);
//		System.out.println(m);


		Market m = hm3.constructMarket(System.getProperty("user.dir") + "/src/hm/market_clearing.txt");
		System.out.println(m.findMarketEquilibrium());
		System.out.println("Done");

	}

}
