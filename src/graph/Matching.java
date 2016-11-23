package graph;

import java.util.ArrayList;

/**
 * Created by moises on 11/21/16.
 */

public class Matching {
	ArrayList<Match> matches;

	public class Match {
		private int i, j;

		public Match(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	public Matching() {
		this.matches = new ArrayList<>();
	}

	public void addMatch(int i, int j) {
		matches.add(new Match(i, j));
	}
}
