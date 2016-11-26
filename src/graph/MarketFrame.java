package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static graph.GraphOps.bfs;

/**
 * Created by moises on 11/25/16.
 */
public class MarketFrame {
	/**
	 * Read market from file. File will contain description of underlying bipartite graph
	 * (including artificial source and sink nodes).
	 *
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Graph constructMarket(String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
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

	/**
	 * @param bipartite: Bipartite graph describing the market frame.
	 *                   Should include artificial source and sink nodes.
	 * @return Returns a matching, which can be either full or a constricted set.
	 */
	public static Matching maxMatching(Graph bipartite) {
		// Create a copy which is a DAG with edges to 1.
		// Same principle as disjoint paths.
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

	/**
	 * Finds the market equilibrium: the prices for the goods being sold.
	 *
	 * @return
	 */
	public static HashMap<Integer, Double> findMarketEquilibriumPrices(Graph market, HashMap<Integer, Double> initialPrices) {
		return findOptimalOutcome(market, initialPrices).sellerPrices;
	}

	/**
	 * Finds the market equilibrium: the optimal matches between the buyers and the goods
	 *
	 * @return
	 */
	public static Matching findMarketEquilibriumMatching(Graph market, HashMap<Integer, Double> initialPrices) {
		return findOptimalOutcome(market, initialPrices).matching;
	}

	/**
	 * Applies VCG mechanism to find prices paid by buyers.
	 *
	 * @param market
	 * @param initialPrices maybe the market has prices that are not optimal yet. Iterate from them.
	 * @return
	 */
	public static HashMap<Integer, Double> VCG(Graph market, HashMap<Integer, Double> initialPrices) {
		Outcome optimal = findOptimalOutcome(market, initialPrices);
		Matching optimalMatching = optimal.matching;
		double sum = 0;
		HashMap<Integer, Double> p_i = new HashMap<>();

		for (int i = 0; i < optimalMatching.matches.size(); i++) {
			Matching.Match m = optimalMatching.matches.get(i);
			double value = market.findAdjacentNode(m.i, m.j).c;
			p_i.put(m.i, value);
			sum += value;
		}

		for (int key : p_i.keySet()) {
			p_i.put(key, -(sum - p_i.get(key)));
		}
		return p_i;
	}

	/**
	 * Implementation of VCG with Clark pivot.
	 *
	 * @param market
	 * @param initialPrices
	 * @return
	 */
	public static HashMap<Integer, Double> VCGClark(Graph market, HashMap<Integer, Double> initialPrices) {
		HashMap<Integer, Double> optimal_pi = VCG(market, initialPrices);
		HashMap<Integer, Double> clark_p_i = new HashMap<>();
		// Get the buyers from our artificial source
		for (Node n : market.adjacents(0)) {
			Graph temp = new Graph(market);
			// Disconnect current node to compute social value when not present.
			temp.disconnectNode(n.ID);
			Outcome optimal = findOptimalOutcome(temp, initialPrices);
			double social_value = 0;
			for (Matching.Match m : optimal.matching.matches) {
				social_value += temp.findAdjacentNode(m.i, m.j).c;
			}

			clark_p_i.put(n.ID, social_value + optimal_pi.get(n.ID));
		}
		return clark_p_i;
	}


	/**
	 * An outcome of applying a particular mechanisms to a market frame
	 */
	private static class Outcome {
		private HashMap<Integer, Double> sellerPrices;
		private Matching matching;

		public Outcome(HashMap<Integer, Double> sellerPrices, Matching matching) {
			this.sellerPrices = sellerPrices;
			this.matching = matching;
		}
	}

	/**
	 * Finds the preferred sellers based on buyers values and sellers prices (utility maximization)
	 *
	 * @param market
	 * @param prices
	 * @return
	 */
	private static Graph preferredSellerGraph(Graph market, HashMap<Integer, Double> prices) {
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
				if (utility >= maxUtility) {
					maxUtility = utility;
					preferredSeller = seller.ID;
				}
			}
			// Remove all bids except the preferred seller
			preferredG.removeExceptUnd(buyer.ID, preferredSeller);
		}
		return preferredG;
	}

	/**
	 * In a bipartite graph, computes how many buyers have placed at least one bid to
	 * the sellers/items.
	 *
	 * @param bipartite
	 * @return
	 */
	private static int bipartitePairs(Graph bipartite) {
		int pairs = 0;
		// Get left component from our artificial source node.
		for (Node n : bipartite.adjacents(0)) {
			if (!bipartite.adjacents(n.ID).isEmpty()) pairs++;
		}
		return pairs;
	}


	private static ArrayList<Integer> highDemand(Matching m, Graph preferredG) {
		ArrayList<Integer> highDemandNodes = new ArrayList<>();
		for (Matching.Match match : m.matches) {
			int degree = preferredG.undirectedDegree(match.j);
			// Have to account for the sink node.
			if (degree - 1 > 1) highDemandNodes.add(match.j);
		}
		return highDemandNodes;
	}

	/**
	 * Computes the social optimum in a market starting with some seller prices.
	 *
	 * @param market
	 * @param prices
	 * @return the optimum outcome
	 */
	private static Outcome findOptimalOutcome(Graph market, HashMap<Integer, Double> prices) {
		// There can't be market equilibrium.
		if (market.size() % 2 != 0) return null;
		HashMap<Integer, Double> tmpPrices = new HashMap<>(prices);
		Graph preferredGraph = preferredSellerGraph(market, tmpPrices);
		Matching matching = maxMatching(preferredGraph);
		while (!matching.isMaximum(bipartitePairs(preferredGraph))) {
			for (int highDemandSeller : highDemand(matching, preferredGraph)) {
				tmpPrices.put(highDemandSeller, tmpPrices.get(highDemandSeller) + 1);
			}
			boolean gtZero = true;
			for (Map.Entry<Integer, Double> entry : tmpPrices.entrySet()) {
				if (entry.getValue() <= 0) {
					gtZero = false;
					break;
				}
			}
			if (gtZero) {
				for (int key : tmpPrices.keySet()) {
					tmpPrices.put(key, tmpPrices.get(key) - 1);
				}
			}
			preferredGraph = preferredSellerGraph(market, tmpPrices);
			matching = maxMatching(preferredGraph);
		}
		return new Outcome(tmpPrices, matching);
	}

	/**
	 * Same as previously used Ford-Fulkerson algorithm, but returning
	 * paths instead of maximum flows.
	 *
	 * @param g
	 * @param source
	 * @param sink
	 * @return
	 */
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

	/**
	 * Takes a generic bipartite graph and "turns on"
	 * artificial source and sink nodes, setting also every directed edge to 1.
	 *
	 * @param bipartite graph.
	 * @return returns a DAG version of the graph, including artificial source and sink.
	 */
	private static Graph getDag(Graph bipartite) {
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
}
