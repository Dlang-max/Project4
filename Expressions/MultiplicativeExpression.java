public class MultiplicativeExpression implements Expression {
    private Expression leftOfSign;
    private Expression rightOfSign;
    private boolean isDivision;

    public MultiplicativeExpression(Expression left, Expression right, boolean division){
        leftOfSign = left;
        rightOfSign = right;
        this.isDivision = division;
    }

    public MultiplicativeExpression(Expression left, Expression right){
        leftOfSign = left;
        rightOfSign = right;
        isDivision = false;
    }

    @Override
    public Expression deepCopy() {
        return new MultiplicativeExpression(leftOfSign.deepCopy(), rightOfSign.deepCopy(), isDivision); 
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }

        return indent + (isDivision ? "/" : "*") + "\n" + leftOfSign.convertToString(indentLevel + 1) + rightOfSign.convertToString(indentLevel + 1);
    }

    @Override
    public double evaluate(double x) {
        if(isDivision){
            return leftOfSign.evaluate(x) / rightOfSign.evaluate(x);
        }

        return leftOfSign.evaluate(x) * rightOfSign.evaluate(x); 
    }

    @Override
    public Expression differentiate() {
        Expression left = new MultiplicativeExpression(leftOfSign.deepCopy().differentiate(), rightOfSign);
        Expression right = new MultiplicativeExpression(leftOfSign, rightOfSign.deepCopy().differentiate());
        
        if(isDivision){
            Expression numerator = new AdditiveExpression(left, right, true);
            Expression denominator = new ExponentialExpression(rightOfSign, new LiteralExpression("2"));
            return new MultiplicativeExpression(numerator, denominator, true);
        }
        return new AdditiveExpression(left, right);
    }    
}