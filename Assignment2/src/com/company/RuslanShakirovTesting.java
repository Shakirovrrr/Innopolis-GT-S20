package com.company;

public class RuslanShakirovTesting {
	private Participant participant1, participant2;
	private int lastMove;
	private int[] fields;

	public static void main(String[] args) {
		Player random = new RandomMoose();
		Player tree = new RuslanShakirovCode();
		Player greedy = new GreedyMoose();

		RuslanShakirovTesting tournament;

		tournament = new RuslanShakirovTesting(random, "Random", tree, "Tree");
		tournament.runTournament(10, 20);
		tournament.reset();
		tournament.swapOrder();
		tournament.runTournament(10, 20);

		tournament = new RuslanShakirovTesting(greedy, "Greedy", tree, "Tree");
		tournament.runTournament(10, 20);
		tournament.reset();
		tournament.swapOrder();
		tournament.runTournament(10, 20);

		tournament = new RuslanShakirovTesting(random, "Random", greedy, "Greedy");
		tournament.runTournament(10, 20);
		tournament.reset();
		tournament.swapOrder();
		tournament.runTournament(10, 20);
	}

	public RuslanShakirovTesting(Player player1, String name1, Player player2, String name2) {
		this.participant1 = new Participant(player1, name1);
		this.participant2 = new Participant(player2, name2);
		this.lastMove = 0;
		this.fields = new int[]{1, 1, 1};
	}

	public RuslanShakirovTesting(Player player1, Player player2) {
		this(player1, "Player 1", player2, "Player 2");
	}

	public static double fGain(int x) {
		return fGrow(x) - fGrow(0);
	}

	public static double fGrow(int x) {
		double exp = Math.exp(x);
		return (10 * exp) / (1 + exp);
	}

	public void reset() {
		this.lastMove = 0;
		this.fields = new int[]{1, 1, 1};
		this.participant1.score = 0;
		this.participant2.score = 0;
		this.participant1.player.reset();
		this.participant2.player.reset();
	}

	public void runTournament(int nGames, int nRounds) {
		System.out.printf("TOURNAMENT OF %d GAMES OF %d ROUNDS\n\n", nGames, nRounds);
		int wins1 = 0, wins2 = 0;

		for (int i = 1; i <= nGames; i++) {
			System.out.println("GAME " + i);
			runGame(nRounds);

			if (participant1.score > participant2.score) {
				wins1++;
			} else if (participant1.score < participant2.score) {
				wins2++;
			}

			reset();
		}

		System.out.println();

		System.out.printf("%s: %d wins of %d (%.2f%%)\n", participant1.name, wins1, nGames, ((double) wins1 / nGames * 100));
		System.out.printf("%s: %d wins of %d (%.2f%%)\n", participant2.name, wins2, nGames, ((double) wins2 / nGames * 100));

		if (wins1 == wins2) {
			System.out.println("DRAW.");
		} else if (wins1 > wins2) {
			System.out.println(participant1.name + " WINS.");
		} else {
			System.out.println(participant2.name + " WINS.");
		}
	}

	public void runSingleGame(int nRounds) {
		System.out.printf("SINGLE GAME OF %d ROUNDS\n\n", nRounds);
		runGame(nRounds);
	}

	private void runGame(int nRounds) {
		assert nRounds > 0;

		for (int i = 0; i < nRounds; i++) {
			makeMoves();
			printStats();
			System.out.println();
		}

		int outcome = Double.compare(participant1.score, participant2.score);
		if (outcome == 0) {
			System.out.println("DRAW.");
		} else if (outcome > 0) {
			System.out.print(participant1.name + " WINS");
		} else {
			System.out.print(participant2.name + " WINS");
		}
		if (outcome != 0) {
			System.out.println(" with advantage of " + Math.abs(participant1.score - participant2.score));
		}
		System.out.println("-----------------------------------------------\n");
	}

	private void makeMoves() {
		int move1 = participant1.player.move(lastMove, fields[0], fields[1], fields[2]);
		int move2 = participant2.player.move(move1, fields[0], fields[1], fields[2]);
		lastMove = move2;

		double gain1 = 0, gain2 = 0;
		if (move1 == move2) {
			updateFields(move1);
		} else {
			gain1 = fGain(fields[move1 - 1]);
			gain2 = fGain(fields[move2 - 1]);

			fields[0]++;
			fields[1]++;
			fields[2]++;
			fields[move1 - 1] = Math.max(fields[move1 - 1] - 2, 0);
			fields[move2 - 1] = Math.max(fields[move2 - 1] - 2, 0);

			participant1.score += gain1;
			participant2.score += gain2;
		}

		System.out.printf("%s -> %d, Gained %f points\n", participant1.name, move1, gain1);
		System.out.printf("%s -> %d, Gained %f points\n", participant2.name, move2, gain2);
	}

	private void doPlayerMove(Participant participant) {
		int move = participant.player.move(lastMove, fields[0], fields[1], fields[2]);
		double gain = 0;
		if (move != lastMove) {
			gain = fGain(fields[move - 1]);
		}
		participant.score += gain;

		System.out.printf("%s -> %d, Gained %f points\n", participant.name, move, gain);

		updateFields(move);
		lastMove = move;
	}

	private void updateFields(int move) {
		fields[0]++;
		fields[1]++;
		fields[2]++;
		fields[move - 1] = Math.max(fields[move - 1] - 2, 0);
	}

	private void printStats() {
		System.out.printf("%s score: %f\n", participant1.name, participant1.score);
		System.out.printf("%s score: %f\n", participant2.name, participant2.score);
		System.out.println("Fields:");
		System.out.printf("| %d | %d | %d |\n", fields[0], fields[1], fields[2]);
	}

	public void swapOrder() {
		Participant tmp = participant1;
		participant1 = participant2;
		participant2 = tmp;
	}

	private static class Participant {
		Player player;
		String name;
		double score;

		public Participant(Player player, String name) {
			this.player = player;
			this.name = name;
			this.score = 0;
		}
	}
}
