package assign1;

public class Problem2 {

	public static void main(String[] args) {

		for(int i = 0; i < 17; i++) {
			if(i < 8) {
				for(int j = 0; j < i;j++)	// 띄어쓰기 출력
					System.out.print(" "); 	
				System.out.print("*");		// 첫 번째 별 출력
				
				for(int k = 14 - 2 * i; k >= 0; k--) // 중간 띄어쓰기 출력
					System.out.print(" ");
				System.out.print("*"); // 두 번째 별 출력
			} 
			else {
				for(int j = i + 1; j < 17; j++)
					System.out.print(" ");
				System.out.print("*");
				
				if(i >= 9) {
					for(int k = 0; k < 2*i - 17; k++)
						System.out.print(" ");
					System.out.print("*");		
				}
			}
						
			System.out.println();	
		}	
	}
}