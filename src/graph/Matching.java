package graph;

import java.util.ArrayList;

/**
 * Created by moises on 11/21/16.
 */

public class Matching {
	protected ArrayList<Match> matches;

	public class Match {
		protected int i, j;

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

	public int size() {
		return matches.size();
	}

	public boolean isMaximum(int nPairs) {
		return size() == nPairs;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (Match m : matches) {
			sb.append("[" + m.i + "-" + m.j + "]");
		}
		sb.append(")");
		return sb.toString();
	}
}
