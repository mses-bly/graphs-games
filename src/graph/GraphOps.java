package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by moises on 10/22/16.
 */
public class GraphOps {
	public static Path bfs(Graph g, int i, int j) {
		boolean[] visited = new boolean[g.getSize()];
		for (int k = 0; k < g.getSize(); k++) {
			Path path = _bfs(g, i, j, visited);
			if (path.exists()) return path;
		}
		return null;
	}

	private static Path _bfs(Graph g, int i, int j, boolean[] visited) {
		Path path = new Path(i, j);
		path.addToPath(i, i, 0);
		if (i == j) return path;
		Queue<Integer> q = new LinkedList<>();
		double[] distances = new double[g.getSize()];
		q.add(i);
		while (!q.isEmpty()) {
			int x = q.poll();
			visited[x] = true;
			for (Node n : g.adjacents(x)) {
				if (!visited[n.ID]) {
					distances[n.ID] += (distances[x] + n.w);
					visited[n.ID] = true;
					path.addToPath(x, n.ID, n.w);
					if (n.ID == j) {
						path.distance = distances[n.ID];
						return path;
					}
					q.add(n.ID);
				}
			}
		}
		return new Path(i, j);
	}

	// Finds the max flow using Ford-Fulkerson algorithm.
	public static double maxFlow(Graph g, int source, int sink) {
		double maxF = 0;
		double it = mfIteration(g, source, sink);
		while(it != -1){
			maxF += it;
			it = mfIteration(g, source, sink);
		}
		return maxF;
	}

	private static double mfIteration(Graph g, int source, int sink) {
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
			return minC;
		}


		return -1;
	}
}
