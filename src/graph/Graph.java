package graph;

import java.util.ArrayList;

/**
 * Created by moises on 9/25/16.
 */
public class Graph {

	//Defines a graph as an adjacency list.
	private ArrayList<Node>[] nodes;
	private int N;

	public Graph(int capacity) {
		nodes = new ArrayList[capacity];
		for (int i = 0; i < capacity; i++) {
			nodes[i] = new ArrayList<>();
		}
		N = capacity;
	}

	public Graph(Graph g) {
		this.N = g.size();
		nodes = new ArrayList[N];
		for (int i = 0; i < N; i++) {
			nodes[i] = new ArrayList<>();
			for (Node n : g.adjacents(i)) {
				nodes[i].add(new Node(n));
			}
		}
	}

	/**
	 * Return the nodes adjacent to graph.Node i
	 *
	 * @param i
	 * @return
	 */
	public ArrayList<Node> adjacents(int i) {
		return nodes[i];
	}

	/**
	 * Connects node i -> j
	 *
	 * @param i
	 * @param j
	 * @param w
	 */
	public void connect(int i, int j, double w, double c) {
		ArrayList<Node> adj = adjacents(i);
		adj.add(new Node(j, w, c));
	}

	public void removeEdge(int i, int j) {
		int idx = -1;
		for (int k = 0; k < adjacents(i).size(); k++) {
			if (adjacents(i).get(k).ID == j) {
				idx = k;
				break;
			}
		}
		if (idx != -1) adjacents(i).remove(idx);
	}

	public void removeExcept(int i, int j) {
		for (Node n : adjacents(i)) {
			if (n.ID != j) {
				removeEdge(i, n.ID);
			}
		}
	}

	public void removeExceptUnd(int i, int j) {
		for (Node n : adjacents(i)) {
			if (n.ID != j) {
				removeEdge(i, n.ID);
				removeEdge(n.ID, i);
			}
		}
	}

	/**
	 * Connects node i -> j and j-> i
	 *
	 * @param i
	 * @param j
	 * @param w
	 */
	public void connectUnd(int i, int j, double w, double c) {
		ArrayList<Node> adj = adjacents(i);
		adj.add(new Node(j, w, c));
		nodes[i] = adj;

		adj = adjacents(j);
		adj.add(new Node(i, w, c));
		nodes[j] = adj;
	}

	public int size() {
		return N;
	}

	public Node findAdjacentNode(int i, int j) {
		for (Node n : adjacents(i)) {
			if (n.ID == j) return n;
		}
		return null;
	}

	private String printAdj(int i) {
		ArrayList<Node> adj = adjacents(i);
		StringBuffer str = new StringBuffer();
		for (Node n : adj) {
			str.append("(" + n.ID + ", " + n.w + "," + n.c + ") -->");
		}
		return str.toString();
	}

	public void print() {
		for (int i = 0; i < N; i++) {
			System.out.println(i + " : " + printAdj(i));
		}
	}
}
