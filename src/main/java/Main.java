import java.util.Scanner;

public class Main {

    private final static Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        // put your code here
        ReplCalculator calculator = new ReplCalculator();
        while (true) {
            var input = SCANNER.nextLine();
            if (input.isBlank()) continue;
            var userInput = analyzeInput(input);
            var result = userInput.process(input, calculator);
            if (result.getOutput() != null) {
                System.out.println(result.getOutput());
            }
            if (!(userInput instanceof Expression)) {
                if (result.isTerminate()) break;
            }
        }
    }

    private static UserInput analyzeInput(String input) {
        if (input.startsWith("/")) return new Command();
        else return new Expression();
    }
}