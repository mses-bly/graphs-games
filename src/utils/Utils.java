package utils;

import java.util.ArrayList;

/**
 * Created by moises on 10/20/16.
 */
public class Utils {

	public static void print(ArrayList<Integer> l) {
		for (int i : l) {
			System.out.print(i + "-");
		}
		System.out.println();
	}

}
