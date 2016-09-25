import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by moises on 9/25/16.
 */
public class HM1 {
	private PrintWriter out;

	public HM1(String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void appendToFile(String s) {
		out.println(s);
	}

	public void appendToFile(int i, int j, double d) {
		if(d >= 0)
			out.printf("[%2d,%2d]  ->   %.6f\n", i, j, d);
		else
			out.printf("[%2d,%2d]  ->  %.6f\n", i, j, d);
	}

	public void close() {
		out.close();
	}

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

	public double avgShortestPath(Graph g, int times, boolean debug) {
		int N = g.size();
		double acc = 0;
		Random r = new Random(System.currentTimeMillis());
		for (int t = 0; t < times; t++) {
			int i = r.nextInt(N);
			int j = r.nextInt(N);
			double d = g.bfs(i, j);
			if (debug) {
				appendToFile(i, j, d);
			}
			// In case that nodes are not connected, ignore them.
			if (d != -1)
				acc += d;
		}
		if (debug) appendToFile("Average shortest path: " + String.format("%.6f", acc / times));
		return acc / times;
	}

//	public double runC() {

//	}


	public static void main(String[] args) {
		HM1 hm1 = new HM1("c.txt");
		Graph g = hm1.connectWithP(20, 0.1);
		g.print();
		hm1.avgShortestPath(g, 10, true);
		hm1.close();
	}
}
