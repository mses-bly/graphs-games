package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moises on 11/23/16.
 */
public class Market {
	private Graph g;

	//Prices of the sold goods
	private HashMap<Integer, Double> sellerPrices;

	public Market(Graph g, HashMap<Integer, Double> sellerPrices) {
		this.g = g;
		this.sellerPrices = sellerPrices;
	}

	private class Outcome {
		private HashMap<Integer, Double> sellerPrices;
		private Matching matching;

		public Outcome(HashMap<Integer, Double> sellerPrices, Matching matching) {
			this.sellerPrices = sellerPrices;
			this.matching = matching;
		}
	}


	private Graph preferredSellerGraph(Graph market, HashMap<Integer, Double> prices) {
		// Create a copy of the graph.
		Graph preferredG = new Graph(market);
		// Get the buyers using our artificial source node.
		ArrayList<Node> buyers = market.adjacents(0);
		// Update the graph by removing the non-preferred sellers
		for (Node buyer : buyers) {
			double maxUtility = Double.MIN_VALUE;
			int preferredSeller = -1;
			// For every seller, figure out which one is the one we want.
			for (Node seller : market.adjacents(buyer.ID)) {
				// find the utility of this bid.
				double utility = seller.c - prices.get(seller.ID);
				if (utility > maxUtility) {
					maxUtility = utility;
					preferredSeller = seller.ID;
				}
			}
			// Remove all bids except the preferred seller
			preferredG.removeExceptUnd(buyer.ID, preferredSeller);
		}
		return preferredG;
	}

	private Outcome findOptimalOutcome(Graph market) {
		// There can't be market equilibrium.
		if (market.size() % 2 != 0) return null;
		HashMap<Integer, Double> prices = new HashMap<>(sellerPrices);
		Graph preferredGraph = preferredSellerGraph(market, prices);
		Matching matching = BiPartiteOps.maxMatching(preferredGraph);
		while (!matching.isMaximum((market.size() - 2) / 2)) {
			for (Matching.Match m : matching.matches) {
				prices.put(m.j, prices.get(m.j) + 1);
			}
			boolean gtZero = true;
			for (Map.Entry<Integer, Double> entry : prices.entrySet()) {
				if (entry.getValue() <= 0) gtZero = false;
			}
			if (gtZero) {
				for (int key : prices.keySet()) {
					prices.put(key, prices.get(key) - 1);
				}
			}
			preferredGraph = preferredSellerGraph(market, prices);
			matching = BiPartiteOps.maxMatching(preferredGraph);
		}
		return new Outcome(prices, matching);
	}

	/**
	 * Finds the market equilibrium: the prices for the goods being sold.
	 *
	 * @return
	 */
	public HashMap<Integer, Double> findMarketEquilibrium() {
		return findOptimalOutcome(this.g).sellerPrices;
	}

	public HashMap<Integer, Double> VCG() {
		Outcome optimal = findOptimalOutcome(this.g);
		Matching optimalMatching = optimal.matching;
		double sum = 0;
		HashMap<Integer, Double> p_i = new HashMap<>();

		for (int i = 0; i < optimalMatching.matches.size(); i++) {
			Matching.Match m = optimalMatching.matches.get(i);
			double value = g.findAdjacentNode(m.i, m.j).c;
			p_i.put(m.i, value);
			sum += value;
		}

		for (int key : p_i.keySet()) {
			p_i.put(key, -(sum - p_i.get(key)));
		}

//		System.out.println(optimal.matching);
//		System.out.println(optimal.sellerPrices);
		return p_i;
	}

	public HashMap<Integer, Double> VCGClark() {
		// Get the buyers from our artificial source
		for (Node n : g.adjacents(0)) {
			Graph temp = new Graph(g);
			temp.disconnectNode(n.ID);
			Outcome optimal = findOptimalOutcome(temp);
		}
//		Outcome optimal = findOptimalOutcome();
//		Matching optimalMatching = optimal.matching;
//		// Compute V(r[-i]).
//		double sum = 0;
//		HashMap<Integer, Double> vr_i = new HashMap<>();
//
//		for (Matching.Match m : optimalMatching.matches) {
//			double value = g.findAdjacentNode(m.i, m.j).c;
//			vr_i.put(m.i, value);
//			sum += value;
//		}
//
//		System.out.println(vr_i);
//		System.out.println(sum);
//
//		for (int key : vr_i.keySet()) {
//			vr_i.put(key, sum - vr_i.get(key));
//		}
//
//		System.out.println(vr_i);
//
//		HashMap<Integer, Double> p_i = VCG();
//
//		System.out.println(p_i);
//
//		for (int key : p_i.keySet()) {
//			p_i.put(key, vr_i.get(key) + p_i.get(key));
//		}
//
//		System.out.println(p_i);
//		return p_i;
		return null;
	}


}
