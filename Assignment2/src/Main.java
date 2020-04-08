import com.company.TrickyMoose;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello");

		TrickyMoose moose = new TrickyMoose();
		int move = moose.move(3, 2, 2, 0);
		System.out.println(move);
	}
}
