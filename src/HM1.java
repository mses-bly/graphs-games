import java.util.Random;

/**
 * Created by moises on 9/25/16.
 */
public class HM1 {

	public Graph connectWithP(int size, double p) {
		Random r = new Random(System.currentTimeMillis());
		Graph g = new Graph(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				//Lets avoid self-loops
				if (i != j) {
					//If p is very high, we want to connect most of the times.
					if (r.nextDouble() <= p)
						g.connect(i, j, 1);
				}
			}
		}
		return g;
	}

	public double avgShortestPath(Graph g, int times) {
		int N = g.size();
		double acc = 0;
		Random r = new Random(System.currentTimeMillis());
		for (int t = 0; t < times; t++) {
			int i = r.nextInt(N);
			int j = r.nextInt(N);
			double d = g.bfs(i, j);
			// In case that nodes are not connected, ignore them.
			if (d != -1)
				acc += d;
		}
		return acc / times;
	}



	public static void main(String[] args) {
		HM1 hm1 = new HM1();
		Graph g = hm1.connectWithP(1000, 0.1);
		g.print();
		System.out.println(hm1.avgShortestPath(g, 2000000));
	}
}
