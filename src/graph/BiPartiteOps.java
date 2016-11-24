package graph;

import java.util.ArrayList;

import static graph.GraphOps.bfs;

/**
 * Created by moises on 11/21/16.
 */
public class BiPartiteOps {
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

	/**
	 * @param bipartite
	 * @return
	 */
	public static Matching maxMatching(Graph bipartite) {
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


}
