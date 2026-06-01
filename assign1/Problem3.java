package assign1;

import java.util.Scanner;

public class Problem3 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		
		for(int i = 1; i <= 9; i++) {
			System.out.printf("%d x %d = %d\n", n,i,n*i);
			
		sc.close();
		}	
	}
}		