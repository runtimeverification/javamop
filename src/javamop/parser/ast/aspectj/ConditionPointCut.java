package javamop.parser.ast.aspectj;

import javamop.parser.ast.expr.*;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class ConditionPointCut extends PointCut {
	
	Expression expr;

	public ConditionPointCut(int line, int column, String type, Expression expr) {
		super(line, column, type);
		this.expr = expr;
	}
	
	public Expression getExpression() { return expr; }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(PointcutVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
