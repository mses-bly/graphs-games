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

	private Graph preferredSellerGraph(HashMap<Integer, Double> prices) {
		// Create a copy of the graph.
		Graph preferredG = new Graph(g);
		// Get the buyers using our artificial source node.
		ArrayList<Node> buyers = g.adjacents(0);
		// Update the graph by removing the non-preferred sellers
		for (Node buyer : buyers) {
			double maxUtility = Double.MIN_VALUE;
			int preferredSeller = -1;
			// For every seller, figure out which one is the one we want.
			for (Node seller : g.adjacents(buyer.ID)) {
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

	public HashMap<Integer, Double> findMarketEquilibrium() {
		// There can't be market equilibrium.
		if (this.g.size() % 2 != 0) return null;
		HashMap<Integer, Double> prices = new HashMap<>(sellerPrices);
		Graph preferredGraph = preferredSellerGraph(prices);
		Matching matching = BiPartiteOps.maxMatching(preferredGraph);
		while (!matching.isMaximum((g.size() - 2) / 2)) {
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
			preferredGraph = preferredSellerGraph(prices);
			matching = BiPartiteOps.maxMatching(preferredGraph);
		}
		return prices;
	}

}
