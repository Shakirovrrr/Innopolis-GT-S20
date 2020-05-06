import com.company.*;

public class Main {
	public static void lol(String[] args) {
		Player random = new RandomMoose();
		Player tricky = new RuslanShakirovCode();
		Player greedy = new GreedyMoose();
		Player madina = new SimpleMoose();

		RuslanShakirovTesting tournament = new RuslanShakirovTesting(madina, "Madina", tricky, "Tricky");
//		Tournament tournament = new Tournament(greedy, "Greedy", tricky, "Tricky");
//		Tournament tournament = new Tournament(random, "Random", tricky, "Tricky");
		tournament.swapOrder();
//		tournament.runSingleGame(20);
		tournament.runTournament(10, 20);
	}
}
