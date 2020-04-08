package com.company;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class TrickyMoose implements Player {
	private Node root;
	private int myLastMove;

	public TrickyMoose() {
		reset();
	}

	@Override
	public void reset() {
		this.root = null;
		this.myLastMove = 0;
	}

	@Override
	public int move(int opponentLastMove, int xA, int xB, int xC) {
		if (opponentLastMove == 0) {
			myLastMove = (new Random().nextInt(3) + 1);
			return myLastMove;
		}

		int[] field = new int[]{xA, xB, xC};

		if (root == null) {
			double payoff = fGain(field[opponentLastMove - 1] + 1) * (opponentLastMove == myLastMove ? 0 : 1);
			root = new Node(xA, xB, xC, false, opponentLastMove, payoff, null);
		} else {
			moveTree(myLastMove);
			moveTree(opponentLastMove);
		}

		growTree(root, 6);

		return predict(root);
	}

	private int predict(Node x) {
		double prevPayoff = x.prev != null ? x.prev.payoff : 0;
		Payoffs[] payoffs = new Payoffs[3];
		payoffs[0] = expectedPayoffs(x.moveA, prevPayoff, x.payoff);
		payoffs[1] = expectedPayoffs(x.moveB, prevPayoff, x.payoff);
		payoffs[2] = expectedPayoffs(x.moveC, prevPayoff, x.payoff);

		return Stream.of(1, 2, 3).max(Comparator.comparingDouble(o -> payoffs[o - 1].my)).get();
	}

	private Payoffs expectedPayoffs(Node x, double myAcc, double opponentAcc) {
		if (x == null) {
			return new Payoffs(myAcc, opponentAcc);
		}

		double prevPayoff = x.prev != null ? x.prev.payoff : 0;
		if (x.wasMyTurn) {
			Payoffs epA = expectedPayoffs(x.moveA, myAcc + x.payoff, opponentAcc + prevPayoff);
			Payoffs epB = expectedPayoffs(x.moveB, myAcc + x.payoff, opponentAcc + prevPayoff);
			Payoffs epC = expectedPayoffs(x.moveC, myAcc + x.payoff, opponentAcc + prevPayoff);
			return Stream.of(epA, epB, epC).max(Comparator.comparingDouble(o -> o.opponent)).get();
		} else {
			Payoffs epA = expectedPayoffs(x.moveA, myAcc + prevPayoff, opponentAcc + x.payoff);
			Payoffs epB = expectedPayoffs(x.moveB, myAcc + prevPayoff, opponentAcc + x.payoff);
			Payoffs epC = expectedPayoffs(x.moveC, myAcc + prevPayoff, opponentAcc + x.payoff);
			return Stream.of(epA, epB, epC).max(Comparator.comparingDouble(o -> o.my)).get();
		}
	}

	private void growTree(Node x, int depth) {
		assert x != null;

		if (depth <= 0 && x.wasMyTurn) {
			return;
		}

		if (x.moveA == null) {
			x.moveA = new Node(x, 1);
		}
		if (x.moveB == null) {
			x.moveB = new Node(x, 2);
		}
		if (x.moveC == null) {
			x.moveC = new Node(x, 3);
		}

		growTree(x.moveA, depth - 1);
		growTree(x.moveB, depth - 1);
		growTree(x.moveC, depth - 1);
	}

	private void moveTree(int move) {
		assert root != null;
		switch (move) {
			case 1:
				root = root.moveA;
				break;
			case 2:
				root = root.moveB;
				break;
			case 3:
				root = root.moveC;
				break;
		}
		root.prev = null;
	}

	static double fGain(int x) {
		return fGrow(x) - fGrow(0);
	}

	static double fGrow(int x) {
		double exp = Math.exp(x);
		return (10 * exp) / (1 + exp);
	}

	static private class Payoffs {
		double my, opponent;

		public Payoffs(double my, double opponent) {
			this.my = my;
			this.opponent = opponent;
		}
	}

	static private class Node {
		int xA, xB, xC;
		boolean wasMyTurn;
		int choice;
		double payoff;
		Node prev, moveA, moveB, moveC;

		public Node(int xA, int xB, int xC, boolean wasMyTurn, int choice, double payoff, Node prev) {
			assert choice >= 1 && choice <= 3;

			this.xA = xA;
			this.xB = xB;
			this.xC = xC;
			this.wasMyTurn = wasMyTurn;
			this.choice = choice;
			this.payoff = payoff;
			this.prev = prev;
		}

		public Node(Node prev, int choice) {
			assert choice >= 1 && choice <= 3;
			assert prev != null;

			this.xA = prev.xA + 1;
			this.xB = prev.xB + 1;
			this.xC = prev.xC + 1;
			this.wasMyTurn = !prev.wasMyTurn;
			this.choice = choice;
			this.prev = prev;

			int[] prevField = new int[]{prev.xA, prev.xB, prev.xC};
			this.payoff = fGain(prevField[choice - 1]) * (prev.choice == choice ? 0 : 1);
			this.payoff += (prev.prev != null ? prev.prev.payoff : 0);

			switch (choice) {
				case 1:
					this.xA = Math.max(prev.xA - 1, 0);
					break;
				case 2:
					this.xB = Math.max(prev.xB - 1, 0);
					break;
				case 3:
					this.xC = Math.max(prev.xC - 1, 0);
					break;
			}
		}
	}
}
