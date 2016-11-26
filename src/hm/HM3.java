package hm;

import graph.Graph;
import graph.Market;
import graph.MarketFrame;
import graph.Matching;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by moises on 11/23/16.
 */
public class HM3 {
	private static void run_10_a() throws FileNotFoundException {
		// Construct the market graph.
		Graph g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/random_bipartite.txt");
		MarketFrame.maxMatching(g);
		Matching m = MarketFrame.maxMatching(g);
		System.out.println("Executing maximum matching in `random_bipartite.txt`");
		System.out.println("Matchings: " + m);
		System.out.println("Matching size: " + m.size());
		System.out.println("Is Maximum: " + m.isMaximum(6));

		// Remove a few edges and find a constrained set.
		g.removeEdge(5, 10);
		g.removeEdge(10, 5);
		m = MarketFrame.maxMatching(g);
		System.out.println("Matchings: " + m);
		System.out.println("Matching size: " + m.size());
		System.out.println("Is Maximum: " + m.isMaximum(6));


	}

	private static void run_10_b() throws FileNotFoundException {
		// Construct the market graph.
		Graph g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7_3.txt");
		HashMap<Integer, Double> initialPrices = new HashMap<Integer, Double>();
//		initialPrices.put(4, 0.0);
//		initialPrices.put(5, 0.0);
//		initialPrices.put(6, 0.0);
//		System.out.println("Finding equilibrium in market `market_7_3.txt`");
//		System.out.println("Equilibrium prices: " + MarketFrame.findMarketEquilibriumPrices(g, initialPrices));
//		System.out.println("Equilibrium matchings: " + MarketFrame.findMarketEquilibriumMatching(g, initialPrices));


		g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_advertisers.txt");
		initialPrices = new HashMap<Integer, Double>();
		initialPrices.put(5, 0.0);
		initialPrices.put(6, 0.0);
		initialPrices.put(7, 0.0);
		initialPrices.put(8, 0.0);
		System.out.println("Finding equilibrium in market `market_advertisers.txt`");
		System.out.println("Equilibrium prices: " + MarketFrame.findMarketEquilibriumPrices(g, initialPrices));
//		System.out.println("Equilibrium matchings: " + MarketFrame.findMarketEquilibriumMatching(g, initialPrices));

	}

	private static void run_11_b() throws FileNotFoundException {
		// Construct the market graph.
		Graph g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7_3.txt");
		HashMap<Integer, Double> initialPrices = new HashMap<Integer, Double>();
		initialPrices.put(4, 0.0);
		initialPrices.put(5, 0.0);
		initialPrices.put(6, 0.0);
		System.out.println("Applying VCG in market `market_7_3.txt`");
		System.out.println("Buyers prices to pay: " + MarketFrame.VCG(g, initialPrices));


		g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_advertisers.txt");
		initialPrices = new HashMap<Integer, Double>();
		initialPrices.put(5, 0.0);
		initialPrices.put(6, 0.0);
		initialPrices.put(7, 0.0);
		initialPrices.put(8, 0.0);
		System.out.println("Applying VCG in market `market_advertisers.txt`");
		System.out.println("Buyers prices to pay: " + MarketFrame.VCG(g, initialPrices));

	}

	private static void run_11_c() throws FileNotFoundException {
		// Construct the market graph.
		Graph g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7_3.txt");
		HashMap<Integer, Double> initialPrices = new HashMap<Integer, Double>();
		initialPrices.put(4, 0.0);
		initialPrices.put(5, 0.0);
		initialPrices.put(6, 0.0);
		System.out.println("Applying VCG in market `market_7_3.txt`");
		System.out.println("Buyers prices to pay: " + MarketFrame.VCGClark(g, initialPrices));


		g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_advertisers.txt");
		initialPrices = new HashMap<Integer, Double>();
		initialPrices.put(5, 0.0);
		initialPrices.put(6, 0.0);
		initialPrices.put(7, 0.0);
		initialPrices.put(8, 0.0);
		System.out.println("Applying VCG in market `market_advertisers.txt`");
		System.out.println("Buyers prices to pay: " + MarketFrame.VCGClark(g, initialPrices));

	}


	private static Graph run_12_a() {
		int buyers = 20;
		int items = 20;
		Graph gr = new Graph(buyers + items + 2);
		// Creating artificial source node.
		for (int i = 1; i <= buyers; i++) {
			gr.connect(0, i, 1, 1);
		}
		// Creating artificial sink node.
		for (int i = 21; i <= buyers + items; i++) {
			gr.connect(i, 41, 1, 1);
		}
		Random r = new Random(System.currentTimeMillis());
		for (int buyer = 1; buyer <= buyers; buyer++) {
			for (int item = 21; item <= buyers + items; item++) {
				// buyer will give us the number of items in the bundle.
				gr.connectUnd(buyer, item, 1, (item - 20) * (r.nextInt(51)));
			}
		}
		return gr;
	}


	public static void main(String[] args) throws FileNotFoundException {
		run_11_b();
	}

}
