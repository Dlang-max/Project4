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
    public Expression differentiate() throws UnsupportedOperationException{

        if(!(power instanceof LiteralExpression)){//If the exponent is not a constant.
            if(!(base instanceof LiteralExpression)) throw new UnsupportedOperationException();
            Expression chain = new MultiplicativeExpression(this, power.deepCopy().differentiate());
            return new MultiplicativeExpression(chain, new LogarithmicExpression(base));
        }

        Expression newExponential = new ExponentialExpression(base, new AdditiveExpression(power, new LiteralExpression("1"), true));
        Expression chain = new MultiplicativeExpression(newExponential, base.deepCopy().differentiate());
        return new MultiplicativeExpression(chain, power);
    }    
}