package hm;

import graph.Graph;
import graph.Market;
import graph.MarketFrame;

import java.io.FileNotFoundException;

/**
 * Created by moises on 11/23/16.
 */
public class HM3 {
	private static void run_10_a() throws FileNotFoundException {
		// Construct the market graph.
		Graph g = MarketFrame.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7.3.txt");
		MarketFrame.maxMatching(g);
		System.out.println(MarketFrame.maxMatching(g));


	}

	public static void main(String[] args) throws FileNotFoundException {
		run_10_a();


//		Graph g = hm3.constructWithCapacity(System.getProperty("user.dir") + "/src/hm/bipartite_1.txt");
//		g.print();
//
//		Matching m = BiPartiteOps.maxMatching(g);
//		System.out.println(m);


//		Market m = hm3.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7.3.txt");
//		System.out.println(m.findMarketEquilibrium());
//		System.out.println("Done");
//
//		Market m = hm3.constructMarket(System.getProperty("user.dir") + "/src/hm/market_7.3.txt");

//		System.out.println(m.VCGClark());

	}

}
