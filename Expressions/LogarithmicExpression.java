public class LogarithmicExpression implements Expression {
    private Expression argument;
    private final static Double BASE = Math.E;

    public LogarithmicExpression(Expression argument){
        this.argument = argument;
    }

    @Override
    public Expression deepCopy() {
        return new LogarithmicExpression(argument.deepCopy()); 
    }

    @Override
    public String convertToString(int indentLevel) {
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += "\t";
        }
        return indent + "log\n" + argument.convertToString(indentLevel + 1);
    }

    @Override
    public double evaluate(double x) {
        return Math.log(argument.evaluate(x)) /  Math.log(BASE); 
    }

    @Override
    public Expression differentiate() {
        return new MultiplicativeExpression(argument.deepCopy().differentiate(), argument, true);
    }    
}