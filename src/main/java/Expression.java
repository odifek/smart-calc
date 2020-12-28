public class Expression implements UserInput {
    @Override
    public Result process(String input, ReplCalculator calculator) {
        try {
            return calculator.processInput(input);
        } catch (UnsupportedOperationException e) {
            return Result.output("Invalid expression");
        }
    }
}
