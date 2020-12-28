import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class ReplCalculator {

    private final Map<String, BigInteger> variables = new HashMap<>();

    private static final String VALID_ASSIGNMENT_REGEX = "^[a-zA-Z]+\\s*=\\s*(|[-+])\\s*([a-zA-Z]+|\\d+)$";
    private static final String ASSIGNMENT_REGEX = ".*=.*";
    private static final String VALID_IDENTIFIER_REGEX = "^[a-zA-Z]+$";
    private final Pattern identifierPattern = Pattern.compile("^\\s*\\w+\\s*$");
    private final Pattern validIdentifier = Pattern.compile(VALID_IDENTIFIER_REGEX);
    private final Pattern validAssignment = Pattern.compile(VALID_ASSIGNMENT_REGEX);
    private final Pattern assignment = Pattern.compile(ASSIGNMENT_REGEX);

    private final Pattern validNumber = Pattern.compile("(^|^[-+])\\d+$");

    public Result processInput(String input) {
        var cleanedInput = cleanInput(input.trim());
        if (assignment.matcher(cleanedInput).matches()) {
            return Result.output(handleAssignment(cleanedInput));
        } else if (validNumber.matcher(cleanedInput).matches()) {
            return Result.output(String.valueOf(Integer.parseInt(cleanedInput)));
        } else if (identifierPattern.matcher(cleanedInput).matches()) {
            return Result.output(handleIdentifier(cleanedInput));
        } else {
            return evaluateExpression(cleanedInput);
        }
    }

    private Result evaluateExpression(String input) {
        var tokens = getTokens(input);

        var postfixCalc = new PostfixCalculator(tokens, variables);
        return postfixCalc.evaluate();
    }

    private String handleIdentifier(String input) {
        if (validIdentifier.matcher(input).matches()) {
            return variables.containsKey(input) ? variables.get(input).toString() : "Unknown variable";
        } else {
            return "Invalid identifier";
        }
    }

    private String handleAssignment(String input) {
        // Extract the variable and the value
        if (!validIdentifier.matcher(getTokens(input).get(0)).matches()) {
            return "Invalid identifier";
        }
        if (!validAssignment.matcher(input).matches()) {
            return "Invalid assignment";
        }
        var assign = input.trim().split("\\s*=\\s*");
        var id = assign[0];
        var value = assign[1];
        String result = null;
        // Tries to convert the value to an int. Otherwise, looks it up as an identifier and assigns the value to the new identifier
        try {
            variables.put(id, new BigInteger(value));
        } catch (NumberFormatException e) {
            var existingValue = variables.get(value);
            if (existingValue != null) {
                variables.put(id, existingValue);
            } else {
                result = "Unknown variable";
            }
        }
        return result;
    }

    List<String> getTokens(String input) {

        return Arrays.asList(cleanInput(input)
                .split("(?<=[\\^)(=*/+-])|(?=[\\^)(=*/+-])"));
    }

    private String cleanInput(String input) {
        String result = input.replaceAll("(?<=[\\^)(=*/+-])\\s+|\\s+(?=[\\^)(=*/+-])", "");
        var allPlus = Pattern.compile("\\+\\++");
        var oddMinus = Pattern.compile("(^|^-)(--)+-");
        var evenMinus = Pattern.compile("(--)+");
        var minusPlus = Pattern.compile("(-\\+)+");
        var plusMinus = Pattern.compile("(\\+-)+");
        do {
            result = allPlus.matcher(result).replaceAll("+");
            result = oddMinus.matcher(result).replaceAll("-");
            result = evenMinus.matcher(result).replaceAll("+");
            result = minusPlus.matcher(result).replaceAll("-");
            result = plusMinus.matcher(result).replaceAll("-");
        } while (allPlus.matcher(result).find()
                || oddMinus.matcher(result).find()
                || evenMinus.matcher(result).find()
                || minusPlus.matcher(result).find()
                || plusMinus.matcher(result).find());

        return result;
    }

    public BigInteger getValueOfVariable(String n) {
        return variables.get(n);
    }
}


interface Operation {
    static Operation add() {
        return new Add();
    }

    static Operation subtract() {
        return new Subtract();
    }

    static Operation multiply() {
        return new Multiply();
    }

    static Operation divide() {
        return new Divide();
    }

    static Operation power() {
        return new Power();
    }

    BigInteger calculate(BigInteger op1, BigInteger op2);

    static Operation get(String operator) {
        switch (operator) {
            case "+":
                return add();
            case "-":
                return subtract();
            case "*":
                return multiply();
            case "/":
                return divide();
            case "^":
                return power();
            default:
                throw new UnsupportedOperationException("Invalid expression");
        }
    }
}

class Add implements Operation {
    @Override
    public BigInteger calculate(BigInteger op1, BigInteger op2) {
        return op1.add(op2);
    }
}

class Subtract implements Operation {
    @Override
    public BigInteger calculate(BigInteger op1, BigInteger op2) {
        return op1.subtract(op2);
    }
}

class Multiply implements Operation {

    @Override
    public BigInteger calculate(BigInteger op1, BigInteger op2) {
        return op1.multiply(op2);
    }
}

class Divide implements Operation {

    @Override
    public BigInteger calculate(BigInteger op1, BigInteger op2) {
        return op1.divide(op2);
    }
}

class Power implements Operation {

    @Override
    public BigInteger calculate(BigInteger op1, BigInteger op2) {
        return op1.modPow(op2, BigInteger.TEN);
    }
}