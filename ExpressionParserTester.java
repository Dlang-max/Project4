import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.io.*;

/**
 * Some code to help you test Project 4.
 */
public class ExpressionParserTester {
	private ExpressionParser _parser;

	@BeforeEach
	/**
	 * Instantiates the actors and movies graphs
	 */
	public void setUp () throws IOException {
		_parser = new SimpleExpressionParser();
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression1 () throws ExpressionParseException {
		final String expressionStr = "x+x";
		final String parseTreeStr = "+\n\tx\n\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression2 () throws ExpressionParseException {
		final String expressionStr = "13*x";
		final String parseTreeStr = "*\n\t13.0\n\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr).convertToString(0));
	}

	@Test	
	public void testExpression3 () throws ExpressionParseException {
		final String expressionStr = "10*x^3 + 2*(15+x)";
		final String parseTreeStr = "+\n\t*\n\t\t10.0\n\t\t^\n\t\t\tx\n\t\t\t3.0\n\t*\n\t\t2.0\n\t\t()\n\t\t\t+\n\t\t\t\t15.0\n\t\t\t\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr).convertToString(0));
	}


	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException1 () {
		try {
			final String expressionStr = "1+2+";
			_parser.parse(expressionStr);
		} catch (ExpressionParseException epe) {
		}
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException2 () {
		try {
			final String expressionStr = "((()))";
			_parser.parse(expressionStr);
		} catch (ExpressionParseException epe) {
		}
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException3 () {
		try {
			final String expressionStr = "()()";
			_parser.parse(expressionStr);
		} catch (ExpressionParseException epe) {
		}
	}

        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate1 () throws ExpressionParseException {
                final String expressionStr = "4*(x+5*x)";
                assertEquals(72, (int) _parser.parse(expressionStr).evaluate(3));
        }

        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate2 () throws ExpressionParseException {
                final String expressionStr = "x";
                assertEquals(2, (int) _parser.parse(expressionStr).evaluate(2));
        }
        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate3 () throws ExpressionParseException {
                final String expressionStr = "9/3*3";
                assertEquals(9, (int) _parser.parse(expressionStr).evaluate(333));
        }
        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate4 () throws ExpressionParseException {
                final String expressionStr = "4-3*x";
                assertEquals(-3.5, _parser.parse(expressionStr).evaluate(2.5), 0.01);
        }
        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate5 () throws ExpressionParseException {
                final String expressionStr = "1./(1. + 5^(-1*x))";
                assertEquals(0.8333333333333334, _parser.parse(expressionStr).evaluate(1), 0.05);
        }
        @Test
        /**
         * Verifies that a specific expression is evaluated correctly.
         */
        public void testEvaluate6 () throws ExpressionParseException {
                final String expressionStr = "4^3^2";
                assertEquals(262144, (int) _parser.parse(expressionStr).evaluate(0));
        }
}
