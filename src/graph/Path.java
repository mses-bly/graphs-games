package graph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moises on 10/20/16.
 */
public class Path {
	public double distance;
	public int source, sink;

	private HashMap<Integer, Integer> path = new HashMap<>();
	ArrayList<Integer> listPath = null;
	ArrayList<Node> nodePath = null;

	public Path(int source, int sink) {
		this.source = source;
		this.sink = sink;
		this.distance = 0;
	}

	public Path(int source, int sink, double distance) {
		this.source = source;
		this.sink = sink;
		this.distance = distance;
	}

	public void addToPath(int parent, int child, double w) {
		path.put(child, parent);
		distance += w;
	}

	public ArrayList<Integer> listPath() {
		if (listPath != null) return listPath;

		if (path.isEmpty()) {
			return null;
		}

		listPath = new ArrayList<Integer>();

		int child = sink;
		int parent = path.get(child);

		listPath.add(0, child);

		//termination condition  is the starting node.
		while (child != parent) {
			listPath.add(0, parent);
			child = parent;
			parent = path.get(child);
		}
		return listPath;
	}

	public ArrayList<Node> nodePath(Graph g) {
		if (nodePath != null) return nodePath;
		listPath();
		if (listPath == null) {
			return null;
		}
		nodePath = new ArrayList<Node>();
		//The first one just gives an ID
		nodePath.add(new Node(listPath.get(0), -1, -1));
		for (int i = 1; i < listPath.size(); i++) {
			nodePath.add(g.findAdjacentNode(listPath.get(i - 1), listPath.get(i)));
		}
		return nodePath;
	}

	public boolean exists(){
		return !path.isEmpty();
	}
}
