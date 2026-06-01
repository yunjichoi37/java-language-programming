package assign1;

import java.util.Scanner;

public class Problem4 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();
		
		// The quick brown fox
		String reverse = "";
		
		for(int i = s.length() - 1; i >= 0; i--)
			reverse = reverse + s.charAt(i);
		
		System.out.println("Input data: " + s);
		System.out.println("Expected Output: " + reverse);
		
		sc.close();
	}
}