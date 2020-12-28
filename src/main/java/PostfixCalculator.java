import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class PostfixCalculator {

    private static final String VALID_IDENTIFIER_REGEX = "^[a-zA-Z]+$";
    private final Pattern validIdentifierPattern = Pattern.compile(VALID_IDENTIFIER_REGEX);

    private final Pattern validNumberPattern = Pattern.compile("(^|^[-+])\\d+$");
    private final Pattern validOperatorPattern = Pattern.compile("^[\\^()/*+-]$");

    private final Stack<String> operators = new Stack<>();
    private final Deque<String> postfixResult = new ArrayDeque<>();
    private final Stack<BigInteger> result = new Stack<>();
    private final List<String> tokens;
    private final Map<String, BigInteger> variables;

    public PostfixCalculator(List<String> tokens, Map<String, BigInteger> variables) {
        this.tokens = tokens;
        this.variables = variables;
    }

    public Result evaluate() {
        clearStack();
        try {
            for (var token : tokens) {
                if (validOperatorPattern.matcher(token).matches()) {
                    updateOperators(token);
                } else if (validNumberPattern.matcher(token).matches() || validIdentifierPattern.matcher(token).matches()) {
                    updateNumberAndVariable(token);
                }
            }
        } catch (Exception e) {
            return Result.output(e.getMessage());
        }
        // Pop remaining operators into the result stack
        while (!operators.isEmpty()) {
            postfixResult.push(operators.pop());
        }
        if (postfixResult.contains("(")) {
            return Result.output("Invalid expression");
        }

        return evaluatePostFixResult();
    }

    private Result evaluatePostFixResult() {
        while (!postfixResult.isEmpty()) {
            var element = postfixResult.pollLast();
            if (validNumberPattern.matcher(element).matches()) {
                result.push(new BigInteger(element));
            } else if (validIdentifierPattern.matcher(element).matches()) {
                var value = variables.get(element);
                if (value == null) return Result.output("Unknown variable");
                result.push(value);
            } else if (validOperatorPattern.matcher(element).matches()) {
                try {
                    var operation = Operation.get(element);
                    var op2 = result.pop();
                    var op1 = result.pop();
                    var calc = operation.calculate(op1, op2);
                    result.push(calc);
                } catch (Exception e) {
                    return Result.output("Invalid expression");
                }
            }
        }
        return Result.output(result.pop().toString());
    }

    private void updateNumberAndVariable(String token) {
        postfixResult.push(token);
    }

    private void updateOperators(String op) {
        if (operators.isEmpty() || "(".equals(operatorsTop()) || "(".equals(op)) {
            operators.push(op);
        } else if (")".equals(op)) {
            while (!operators.isEmpty() && !"(".equals(operatorsTop())) {
                postfixResult.push(operators.pop());
            }
            if (!operators.isEmpty() && "(".equals(operatorsTop())) {
                operators.pop();
            } else {
                throw new RuntimeException("Invalid expression");
            }
        } else if (comparePrecedence(op, operatorsTop()) > 0) {
            operators.push(op);
        } else if (comparePrecedence(op, operatorsTop()) <= 0) {
            while (!operators.isEmpty() && !"(".equals(operatorsTop()) && comparePrecedence(op, operatorsTop()) <= 0) {
                postfixResult.push(operators.pop());
            }
            operators.push(op);
        } else {
            throw new UnsupportedOperationException("Unknown operator " + op);
        }
    }

    private String operatorsTop() {
        return operators.peek();
    }

    private int comparePrecedence(String op, String other) {
        return getPrecedence(op) - getPrecedence(other);
    }

    private int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 2;
            case "*":
            case "/":
                return 10;
            case "^":
                return 11;
            default:
                throw new UnsupportedOperationException("Invalid operator");
        }
    }

    private void clearStack() {
        operators.clear();
        postfixResult.clear();
    }
}
