import java.util.function.*;
/*
* Grammar:
* S -> A | P
* A -> A+M | A-M | M
* M -> M*E | M/E | E
* E -> P^E | P | log(P)
* P -> (S) | L | V
* L -> <float>
* V -> x
*/

public class SimpleExpressionParser implements ExpressionParser {

	/**
	* Attempts to create an expression tree from the specified String.
	* Throws a ExpressionParseException if the specified string cannot be parsed.
	* 
	* @param str the string to parse into an expression tree
	* @return the Expression object representing the parsed expression tree
	*/
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseAdditiveExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}
	
	protected Expression parseAdditiveExpression (String str) {
		
		// A -> A+M | A-M | M

		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '+' || str.charAt(i) == '-'){
				Expression left = parseAdditiveExpression(str.substring(0, i));
				Expression right = parseAdditiveExpression(str.substring(i + 1));

				if(left == null || right == null) {
					return null;
				}
				else if(str.charAt(i) == '+') {
					return new AdditiveExpression(left, right);
				}
				else {
					return new AdditiveExpression(left, right, true);
				}
			} 
			else {
				Expression expression = parseMultiplicativeExpression(str);
				if(expression != null) {
					return expression;
				}
			}
			
		}
		return null;
	}

	protected Expression parseMultiplicativeExpression (String str) {
		
		// * M -> M*E | M/E | E

		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '*' || str.charAt(i) == '/'){
				Expression left = parseMultiplicativeExpression(str.substring(0, i));
				Expression right = parseMultiplicativeExpression(str.substring(i + 1));

				if(left == null || right == null) {
					return null;
				}
				else if(str.charAt(i) == '*') {
					return new MultiplicityExpression(left, right);
				}
				else {
					return new MultiplicityExpression(left, right, true);
				}
			} else {
				Expression expression = parseExponentialExpression(str);
				if(expression != null) {
					return expression;
				}
			}
		}
		return null;
	}

	protected Expression parseExponentialExpression (String str) {
		
		// * E -> P^E | P | log(P)

		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '^'){
				Expression base = parseExponentialExpression(str.substring(0, i));
				Expression power = parseExponentialExpression(str.substring(i + 1));

				if(base == null || power == null) {
					return null;
				} 
				return new ExponentialExpression(base, power);

			} else if (str.contains("log(") && str.substring(0, 4).equals("log(") && str.charAt(str.length() - 1) == ')') {
				Expression expression = parseExponentialExpression(str.substring(4, str.length() - 1));
				if(expression != null) {
					return new LogarithmicExpression(expression);
				}
			} else {
				Expression expression = parseParentheticalExpression(str);
				if(expression != null) {
					return expression;
				}
				
			}
		}
		return null;
	}

	protected Expression parseParentheticalExpression (String str) {
		
		// P -> (S) | L | V

		if(str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')') {

			Expression expression = parseAdditiveExpression(str.substring(1, str.length() - 1));
			if(expression != null) {
				return new ParentheticalExpression(expression);
			}
		} else {
			Expression expression = parseLiteralExpression(str);
			if(expression != null) {
				return expression;
			}
			expression = parseVariableExpression(str);
			if(expression != null) {
				return expression;
			}
		}
		return null;
	}

	// Variable
	protected Expression parseVariableExpression (String str) {
		if (str.equals("x")) {
			return new VariableExpression(str);
		}
		return null;
	}

	// Literal
	protected Expression parseLiteralExpression (String str) {

		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			return new LiteralExpression(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("x^(5*x) - 1").convertToString(0));
	}
}
