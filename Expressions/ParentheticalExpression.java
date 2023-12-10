public class ParentheticalExpression implements Expression {
    private Expression expression;

    public ParentheticalExpression(Expression expression){
        this.expression = expression;
    }

    @Override
    public Expression deepCopy() {
        return new ParentheticalExpression(expression.deepCopy()); 
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }

        

        return indent + "()\n" + expression.convertToString(indentLevel + 1);
    }

    @Override
    public double evaluate(double x) {
        return expression.evaluate(x);
    }

    @Override
    public Expression differentiate() {
        return expression.differentiate();
    }    
}