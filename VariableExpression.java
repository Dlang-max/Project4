public class VariableExpression implements Expression {

    private String expressionString;

    public VariableExpression(String str){
        expressionString = str;
    }

    @Override
    public Expression deepCopy() {
        return new VariableExpression("x");
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }
        return indent + expressionString + "\n";
    }

    @Override
    public double evaluate(double x) {
        return x; 
    }

    @Override
    public Expression differentiate() {
        return new LiteralExpression("1");
    }    
}