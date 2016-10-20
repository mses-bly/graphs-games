package graph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by moises on 10/20/16.
 */
public class Path {
	public double distance = 0;
	private HashMap<Integer, Integer> path = new HashMap<>();

	public void addToPath(int parent, int child) {
		path.put(child, parent);
	}

	public ArrayList<Integer> recoverPath(int dest) {
		ArrayList<Integer> p = new ArrayList<Integer>();
		if (path.isEmpty()) return null;

		int child = dest;
		int parent = path.get(child);

		p.add(0, child);

		//termination condition  is the starting node.
		while (child != parent) {
			p.add(0, parent);
			child = parent;
			parent = path.get(child);
		}
		return p;
	}


}
