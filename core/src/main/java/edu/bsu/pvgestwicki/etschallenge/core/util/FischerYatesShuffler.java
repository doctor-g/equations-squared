package edu.bsu.pvgestwicki.etschallenge.core.util;

import java.util.Random;

public final class FischerYatesShuffler {

	private static final Random RANDOM = new Random();

	public static void shuffle(char[] seq) {
		for (int i = seq.length - 1; i > 0; i--) {
			int pos = RANDOM.nextInt(i);
			swap(seq, i, pos);
		}
	}

	private static void swap(char[] seq, int i, int j) {
		char t = seq[i];
		seq[i] = seq[j];
		seq[j] = t;
	}

}
