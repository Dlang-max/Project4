public class LiteralExpression implements Expression {

    private String expressionString;

    public LiteralExpression(String str){
        expressionString = str;
    }

    @Override
    public Expression deepCopy() {
        return new LiteralExpression(expressionString);
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }
        return indent + Double.valueOf(expressionString) + "\n";
    }

    @Override
    public double evaluate(double x) {
        return Double.valueOf(expressionString);
    }

    @Override
    public Expression differentiate() {
        return new LiteralExpression("0");
    }    
}