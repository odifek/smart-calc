public class Result {

    private final String output;
    private final boolean terminate;

    private Result(String output, boolean terminate) {

        this.output = output;
        this.terminate = terminate;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public String getOutput() {
        return output;
    }

    public static Result terminate(String message) {
        return new Result(message, true);
    }

    public static Result output(String message) {
        return new Result(message, false);
    }
}
