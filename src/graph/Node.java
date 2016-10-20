package graph;

/**
 * Defines a node in the graph.
 */
public class Node {

	// Defines an ID for this node
	int ID;

	//	Defines the weight of the incoming edge
	double w;

	public Node(int ID, double w) {
		this.ID = ID;
		this.w = w;
	}
}
