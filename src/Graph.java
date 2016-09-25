import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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

	/**
	 * Return the nodes adjacent to Node i
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
	public void connect(int i, int j, double w) {
		ArrayList<Node> adj = adjacents(i);
		adj.add(new Node(j, w));
	}

	/**
	 * Connects node i -> j and j-> i
	 *
	 * @param i
	 * @param j
	 * @param w
	 */
	public void connectUnd(int i, int j, double w) {
		ArrayList<Node> adj = adjacents(i);
		adj.add(new Node(j, w));
		nodes[i] = adj;

		adj = adjacents(j);
		adj.add(new Node(i, w));
		nodes[j] = adj;
	}


	public double bfs(int i, int j) {
		boolean[] visited = new boolean[N];
		for (int k = 0; k < N; k++) {
			double dist = _bfs(i, j, visited);
//			for (int m = 0; m < N; m++) {
//				System.out.print(visited[m] + " ");
//			}
			if (dist != -1) return dist;
//			System.out.println();
		}
		return -1;
	}

	private double _bfs(int i, int j, boolean[] visited) {
		if (i == j) return 0;
		Queue<Integer> q = new LinkedList<>();
		double[] distances = new double[N];
		q.add(i);
		while (!q.isEmpty()) {
			int x = q.poll();
			visited[x] = true;
			for (Node n : adjacents(x)) {
				if (!visited[n.ID]) {
					distances[n.ID] += (distances[x] + n.w);
					visited[n.ID] = true;
					if (n.ID == j) return distances[n.ID];
					q.add(n.ID);
				}
			}
		}
		return -1;
	}

	public int size() {
		return N;
	}


	private String printAdj(int i) {
		ArrayList<Node> adj = adjacents(i);
		StringBuffer str = new StringBuffer();
		for (Node n : adj) {
			str.append("(" + n.ID + ", " + n.w + ") -->");
		}
		return str.toString();
	}

	public void print() {
		for (int i = 0; i < N; i++) {
			System.out.println(i + " : " + printAdj(i));
		}
	}

	public static void main(String[] args) {
		Graph g = new Graph(8);
		g.connect(0, 1, 1);
		g.connect(1, 0, 1);
		g.connect(1, 2, 1);
		g.connect(2, 3, 1);
		g.connect(2, 4, 1);
		g.connect(3, 2, 1);
//		g.connect(3, 7, 1);
		g.connect(3, 5, 1);
		g.connect(4, 5, 1);
		g.connect(5, 4, 1);
		g.connect(6, 7, 1);

//		System.out.println(g.bfs(0, 1));
//		System.out.println(g.bfs(0, 2));
//		System.out.println(g.bfs(0, 6));
		System.out.println(g.bfs(6, 7));
//		System.out.println(g.bfs(3, 1));
//		System.out.println(g.bfs(1, 7));
//		System.out.println(g.bfs(2, 7));
//		System.out.println(g.bfs(2, 7));
	}
}
