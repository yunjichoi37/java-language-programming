package assign1;

public class Problem1 {

	public static void main(String[] args) {
		// Problem 1
		
		int die1 = 0, die2 = 0; // init
		
		while (die1 + die2 != 7) {
			die1 = (int)(7 * Math.random()) + 1;
			die2 = (int)(7 * Math.random()) + 1;
			System.out.printf("(%d,%d) ", die1, die2);
		}	
	}
}