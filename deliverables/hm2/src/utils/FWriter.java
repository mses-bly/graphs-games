package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by moises on 10/20/16.
 */
public class FWriter {

	private PrintWriter out = null;

	public FWriter(String fileName) {
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
		if (d >= 0)
			out.printf("[%4d,%4d]  ->   %.6f\n", i, j, d);
		else
			out.printf("[%4d,%4d]  ->  %.6f\n", i, j, d);
	}

	public void appendToFile(double i, double j) {
		out.printf("%.6f, %.6f\n", i, j);
	}

	public void close() {
		out.close();
	}

}
