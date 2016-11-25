package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
