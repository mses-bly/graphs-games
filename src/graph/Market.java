package graph;

import java.util.ArrayList;

/**
 * Created by moises on 11/23/16.
 */
public class Market {
	private Graph g;
	private int[] values;

	public Market(Graph g, int[] values) {
		this.g = g;
		this.values = values;
	}

	public Graph preferredSeller(){
		// Create a copy of the graph.
		Graph preferredG = new Graph(g);
		// Get the buyers using our artificial source node.
		ArrayList<Node> buyers = g.adjacents(0);
		// Update the graph by removing the non-preferred sellers
		for(Node  buyer : buyers){
			int maxUtility   = Integer.MIN_VALUE;
			int preferredSeller = -1;
			// For every seller, figure out which one is the one we want.
			for(Node seller : preferredG.adjacents(buyer.ID)){
				int utility = values[buyer.ID] - values[seller.ID];
				if(utility > maxUtility){
					maxUtility = utility;
					preferredSeller = seller.ID;
				}
			}
		}



		return null;
	}
}
