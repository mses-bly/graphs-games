package graph;

/**
 * Defines a node in the graph.
 */
public class Node {

	//	Defines an ID for this node
	public int ID;

	//	Defines the weight of the incoming edge
	public double w;

	//	Defines the capacity of the incoming edge
	public double c;

	public Node(int ID, double w) {
		this.ID = ID;
		this.w = w;
		this.c = 1;
	}

	public Node(int ID, double w, double c) {
		this.ID = ID;
		this.w = w;
		this.c = c;
	}

	public Node(Node other) {
		this.ID = other.ID;
		this.w = other.w;
		this.c = other.c;
	}

	@Override
	public boolean equals(Object obj) {
		return this.ID == ((Node) obj).ID;
	}
}
