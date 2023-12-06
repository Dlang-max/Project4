public class ExponentialExpression implements Expression {
    private Expression base;
    private Expression power;

    public ExponentialExpression(Expression base, Expression power){
        this.base = base;
        this.power = power;
    }

    @Override
    public Expression deepCopy() {
        return new ExponentialExpression(base.deepCopy(), power.deepCopy()); 
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }
        return indent + "^\n" + base.convertToString(indentLevel + 1) + power.convertToString(indentLevel + 1);
    }

    @Override
    public double evaluate(double x) {
        return Math.pow(base.evaluate(x), power.evaluate(x)); 
    }

    @Override
    public Expression differentiate() {
        return null; //todo
    }    
}