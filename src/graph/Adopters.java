package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by moises on 10/26/16.
 * Assumes undirected graph.
 */
public class Adopters {
	private Graph g;
	private double q;
	private HashMap<Integer, Integer> degree;
	private HashSet<Integer> infected;

	public Adopters(Graph g, double q) {
		this.g = g;
		this.q = q;
		findDegree();
		this.infected = new HashSet<>();
	}

	public HashSet<Integer> earlyAdopters() {
		while (true) {
			if (GraphOps.contagion(g, infected, q).size() == g.size()) return infected;
			int start = findHighest();
			System.out.println(start);
			if (start == -1) return infected;
			infected.add(start);
			for (Node adj : g.adjacents(start)) {
				infect(adj.ID);
//				if (GraphOps.contagion(g, infected, q).size() == g.size()) return infected;
			}
		}
	}

	public void infect(int node) {
		if (infected.contains(node)) return;
		while (decideInfection(node)) {
			int target = findHighest(node);
			if (target == -1) {
				infected.add(node);
				return;
			} else {
				infected.add(target);
			}
		}
	}


	public boolean decideInfection(int node) {
		if (infected.contains(node)) return false;
		int infectedCount = 0;
		for (Node adj : g.adjacents(node)) {
			if (infected.contains(adj.ID)) infectedCount++;
		}
		if ((double) infectedCount / g.adjacents(node).size() >= q) {
			return false;
		}
		return true;
	}


	public int findHighest() {
		int maxN = -1;
		int maxD = Integer.MIN_VALUE;

		for (Map.Entry<Integer, Integer> e : degree.entrySet()) {
			if (!infected.contains(e.getKey()) && e.getValue() > maxD) {
				maxD = e.getValue();
				maxN = e.getKey();
			}
		}
		return maxN;
	}

	public int findHighest(int node) {
		int maxN = -1;
		int maxD = Integer.MIN_VALUE;

		for (Node adj : g.adjacents(node)) {
			if (!infected.contains(adj.ID) && degree.get(adj.ID) > maxD) {
				maxD = degree.get(adj.ID);
				maxN = adj.ID;
			}
		}
		return maxN;
	}


	private void findDegree() {
		degree = new HashMap<>();
		for (int i = 0; i < g.size(); i++) {
			degree.put(i, g.adjacents(i).size());
		}
	}
}
