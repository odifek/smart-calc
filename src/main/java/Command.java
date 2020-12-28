public class Command implements UserInput {
    @Override
    public Result process(String input, ReplCalculator calculator) {
        switch (input) {
            case "/exit": {
                return Result.terminate("Bye!");
            }
            case "/help": {
                return Result.output("The program calculates the sum and difference of numbers\nYou can assign variables such as a = 2.\nYou can also reuse variables in your expressions");
            }
            default: {
                return Result.output("Unknown command");
            }
        }
    }
}
