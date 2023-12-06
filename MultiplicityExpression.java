public class MultiplicityExpression implements Expression {
    private Expression leftOfSign;
    private Expression rightOfSign;
    private boolean isDivision;

    public MultiplicityExpression(Expression left, Expression right, boolean division){
        leftOfSign = left;
        rightOfSign = right;
        this.isDivision = division;
    }

    public MultiplicityExpression(Expression left, Expression right){
        leftOfSign = left;
        rightOfSign = right;
        isDivision = false;
    }

    @Override
    public Expression deepCopy() {
        return new MultiplicityExpression(leftOfSign.deepCopy(), rightOfSign.deepCopy(), isDivision); 
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
        return null; //todo
    }    
}