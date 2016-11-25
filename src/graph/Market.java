package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static graph.GraphOps.bfs;

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

	private Graph getDag(Graph bipartite) {
		Graph residual = new Graph(bipartite);
		ArrayList<Node> leftPartition = residual.adjacents(0);

		for (Node left : leftPartition) {
			left.c = 1;
			for (Node right : residual.adjacents(left.ID)) {
				right.c = 1;
				residual.removeEdge(right.ID, left.ID);
				for (Node sink : residual.adjacents(right.ID))
					sink.c = 1;
			}
		}
		return residual;
	}

	/**
	 * @param bipartite
	 * @return
	 */
	public Matching maxMatching(Graph bipartite) {
		// Create a copy that sets every edge to 1
		Graph residual = getDag(bipartite);
		Matching m = new Matching();
		Path it = matchingIteration(residual, 0, residual.size() - 1);
		while (it != null) {
			ArrayList<Integer> path = it.listPath();
			m.addMatch(
					path.subList(1, path.size() - 1).get(0),
					path.subList(1, path.size() - 1).get(1)
			);
			it = matchingIteration(residual, 0, residual.size() - 1);
		}
		return m;
	}

	private static Path matchingIteration(Graph g, int source, int sink) {
		Path p = bfs(g, source, sink);
		if (p != null && p.exists()) {
			ArrayList<Node> path = p.nodePath(g);

			double minC = Double.MAX_VALUE;

			//find min flow
			for (int i = 1; i < path.size(); i++) {
				if (path.get(i).c < minC) minC = path.get(i).c;
			}

			//update residual graph
			for (int i = 1; i < path.size(); i++) {
				Node n = path.get(i);
				n.c -= minC;
				if (n.c <= 0) {
					// remove edge;
					g.removeEdge(path.get(i - 1).ID, n.ID);
				}
				//add another edge or add to existing one.
				Node aux = g.findAdjacentNode(n.ID, path.get(i - 1).ID);

				if (aux != null) aux.c += minC;
				else {
					g.connect(n.ID, path.get(i - 1).ID, n.w, minC);
				}
			}
			return p;
		}
		return null;
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


	private int bipartitePairs(Graph bipartite) {
		int pairs = 0;
		// Get left component from our artificial source node.
		for (Node n : bipartite.adjacents(0)) {
			if(!bipartite.adjacents(n.ID).isEmpty()) pairs++;
		}
		return pairs;
	}

	private Outcome findOptimalOutcome(Graph market) {
		// There can't be market equilibrium.
		if (market.size() % 2 != 0) return null;
		HashMap<Integer, Double> prices = new HashMap<>(sellerPrices);
		Graph preferredGraph = preferredSellerGraph(market, prices);
		Matching matching = maxMatching(preferredGraph);
		while (!matching.isMaximum(bipartitePairs(preferredGraph))) {
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
			matching = maxMatching(preferredGraph);
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
		HashMap<Integer, Double> optimal_pi = VCG();
		HashMap<Integer, Double> clark_p_i = new HashMap<>();
		// Get the buyers from our artificial source
		for (Node n : g.adjacents(0)) {
			Graph temp = new Graph(g);
			// Disconnect current node to compute value when not present.
			temp.disconnectNode(n.ID);
			Outcome optimal = findOptimalOutcome(temp);

			double social_value = 0;
			for(Matching.Match m : optimal.matching.matches){
				social_value += temp.findAdjacentNode(m.i, m.j).c;
			}

			clark_p_i.put(n.ID, social_value + optimal_pi.get(n.ID));
		}
		return clark_p_i;
	}


}
