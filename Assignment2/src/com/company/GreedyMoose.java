package com.company;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class GreedyMoose implements Player {
	private static int argMax(int... xs) throws NoSuchElementException {
		return IntStream.range(0, xs.length).boxed().max(Comparator.comparingInt(o -> xs[o])).get();
	}

	@Override
	public void reset() {

	}

	@Override
	public int move(int opponentLastMove, int xA, int xB, int xC) {
		return argMax(xA, xB, xC) + 1;
	}

	@Override
	public String getEmail() {
		return null;
	}
}
