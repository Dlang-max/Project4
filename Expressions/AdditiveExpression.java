public class AdditiveExpression implements Expression {
    private Expression leftOfSign;
    private Expression rightOfSign;
    private boolean isSubtraction;

    public AdditiveExpression(Expression left, Expression right, boolean subtraction){
        leftOfSign = left;
        rightOfSign = right;
        this.isSubtraction = subtraction;
    }

    public AdditiveExpression(Expression left, Expression right){
        leftOfSign = left;
        rightOfSign = right;
        isSubtraction = false;
    }

    @Override
    public Expression deepCopy() {
        return new AdditiveExpression(leftOfSign.deepCopy(), rightOfSign.deepCopy(), isSubtraction); 
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }
        
        return indent + (isSubtraction ? "-" : "+") + "\n" + leftOfSign.convertToString(indentLevel + 1) + rightOfSign.convertToString(indentLevel + 1);
    }

    @Override
    public double evaluate(double x) {
        if(isSubtraction){
            return leftOfSign.evaluate(x) - rightOfSign.evaluate(x);
        }
        return leftOfSign.evaluate(x) + rightOfSign.evaluate(x); 
    }

    @Override
    public Expression differentiate() {
        return new AdditiveExpression(leftOfSign.differentiate(), rightOfSign.differentiate(), isSubtraction);
    }    
}