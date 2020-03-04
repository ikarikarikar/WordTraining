import java.util.Scanner;
import java.util.regex.Pattern;

public class Trainer {

	public void addtack() {
		
		Scanner skaner = new Scanner(System.in);
		String inputString;	
		
		while (true) {			
			System.out.println("Enter a task in the following format or stop(S)");
			System.out.println("question% answer1# answer...# answerX%");
			inputString = skaner.nextLine();						
			if (inputString.equals("S")) break;
			if (!Pattern.matches("^.+?%(.+?#)*.+?%$", inputString )) { 
				System.out.println("Wrong task format");
			}	
		}	
		skaner.close();
	}
	
}	

