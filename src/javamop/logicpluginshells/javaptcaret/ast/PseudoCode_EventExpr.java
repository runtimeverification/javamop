package javamop.logicpluginshells.javaptcaret.ast;

import javamop.logicpluginshells.javaptcaret.visitor.DumpVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.GenericVisitor;
import javamop.logicpluginshells.javaptcaret.visitor.VoidVisitor;

public class PseudoCode_EventExpr extends PseudoCode_Expr {
	String name;

	public PseudoCode_EventExpr(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	@Override
	public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public String toString() {
		DumpVisitor visitor = new DumpVisitor();
		String formula = accept(visitor, null);
		return formula;
	}
}
