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
/**
 * A simple expression parser that can parse expressions containing the following:
 * 
 * - Addition and subtraction (+ and -)
 * - Multiplication and division (* and /)
 * - Exponentiation (^)
 * - Logarithms (log)
 * - Parentheses ()
 * - Variables (x)
 * - Floating point numbers ([0.0-10.0)+)
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
		Expression expression = parseExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}
		return expression;
	}

	/**
	 * Parses an expression with two parts Used as a helper in combination with individual expression parsers.
	 * 
	 * @param opp1 the first operator, used with "^"
	 * @param str the string to parse
	 * @param A the function to parse the first part
	 * @param M the function to parse the second part
	 * @param expressionCreator the function to create the expression
	 * @return the expression if it can be parsed, null otherwise
	 */
	public Expression parseTwoPartExpression(char opp1, String str, Function<String, Expression> A, Function<String, Expression> M, 
	BiFunction<Expression, Expression, Expression> expressionCreator) {
		return parseTwoPartExpression(opp1, opp1, str, A, M, expressionCreator, null);
	}

	/**
	 * Parses an expression with two parts. Used as a helper in combination with individual expression parsers.
	 * @param opp1 the first operator, such as "+"
	 * @param opp2 the second operator, such as "-"
	 * @param str the string to parse
	 * @param A the function to parse the first part
	 * @param M the function to parse the second part
	 * @param expressionCreator the function to create the expression
	 * @param expressionCreator2 the function to create the expression. Applies a true boolean as third parameter if opp2 matches.
	 * @return the expression if it can be parsed, null otherwise
	 */
	public Expression parseTwoPartExpression(char opp1, char opp2, String str, Function<String, Expression> A, Function<String, Expression> M, 
	BiFunction<Expression, Expression, Expression> expressionCreator,
	TriFunction<Expression, Expression, Boolean, Expression> expressionCreator2) {

		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == opp1 || str.charAt(i) == opp2){
				Expression left = A.apply(str.substring(0, i));
				Expression right = M.apply(str.substring(i + 1));

				if(left != null && right != null) {
					if(str.charAt(i) == opp1) {
						return expressionCreator.apply(left, right);
					}

					return expressionCreator2.apply(left, right, true);
				}
			}
		}

		return null; 
	}

	/**
	 * Parses a single part expression. Used as a helper in combination with individual expression parsers.
	 * @param str the String being parsed
	 * @param A the function to parse the expression
	 * @param expressionCreator the function to create the expression
	 * @return the expression if it can be parsed, null otherwise
	 */
	public Expression parseSinglePartExpression(String str, Function<String, Expression> A, Function<Expression, Expression> expressionCreator) {
		
		Expression expression = A.apply(str);
		if(expression != null) {
			return expressionCreator.apply(expression);
		}
		return null;
	}


	/**
	 * Parses the starting symbol for the production rules of the CFG.
	 * @param str the String being parsed
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseExpression (String str) {
		// S -> A | P

		Expression expression = parseAdditiveExpression(str);
		if(expression != null) {
			return expression;
		}
		expression = parseParentheticalExpression(str);
		if(expression != null) {
			return expression;
		}

		return null;
	}

	/**
	 * Parses an addition or subtraction expression.
	 * @param str the String being parsed
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseAdditiveExpression (String str) {
		
		// A -> A+M | A-M | M
		Expression expression = parseTwoPartExpression('+', '-', str, this::parseAdditiveExpression, this::parseMultiplicativeExpression, AdditiveExpression::new, AdditiveExpression::new);
		if(expression != null) {
			return expression;
		}
		return parseMultiplicativeExpression(str);
	}

	/**
	 * Parses a multiplication or division expression.
	 * @param str the string representing the expression being parsed.
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseMultiplicativeExpression (String str) {
		
		// M -> M*E | M/E | E
		Expression expression = parseTwoPartExpression('*', '/', str, this::parseMultiplicativeExpression, this::parseExponentialExpression, MultiplicativeExpression::new, MultiplicativeExpression::new);
		if(expression != null) {
			return expression;
		}

		return parseExponentialExpression(str);
	}

	/**
	 * Parses exponential and logarithmic expressions.
	 * @param str the string representing the expression being parsed.
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseExponentialExpression (String str) {
		
		// * E -> P^E | P | log(P)
		Expression expression = parseTwoPartExpression('^', str, this::parseParentheticalExpression, this::parseExponentialExpression, ExponentialExpression::new);
		if(expression != null) {
			return expression;
		}


		if (str.contains("log(") && str.substring(0, 4).equals("log(") && str.charAt(str.length() - 1) == ')') {
			expression = parseSinglePartExpression(str.substring(3), this::parseParentheticalExpression, LogarithmicExpression::new);
			if(expression != null) {
				return expression;
			}
		}
			
		return parseParentheticalExpression(str);
	}

	/**
	 * Parses a parenthetical expression.
	 * 
	 * @param str the string representing the expression being parsed.
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseParentheticalExpression (String str) {
		
		// P -> (S) | L | V

		if(str.length() >= 3 && str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')') {
			Expression expression = parseExpression(str.substring(1, str.length() - 1));
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

	/**
	 * Parses a variable expression.
	 * @param str the string representing the expression being parsed.
	 * @return parsed expression if possible, null otherwise.
	 */
	protected Expression parseVariableExpression (String str) {
		if (str.equals("x")) {
			return new VariableExpression(str);
		}
		return null;
	}

	/**
	 * Parses a literal expression.
	 * @param str the string representing the expression being parsed.
	 * @return parsed expression if possible, null otherwise.
	 */
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
		System.out.println(parser.parse("10*x^3 + 2*(15+x)").convertToString(0));
	}
}
