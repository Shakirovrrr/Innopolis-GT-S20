package com.company;

import java.util.Random;

public class RandomMoose implements Player {
	@Override
	public void reset() {

	}

	@Override
	public int move(int opponentLastMove, int xA, int xB, int xC) {
		return (new Random().nextInt(3) + 1);
	}
}
