package logicrepository.plugins.ptcaret.ast;

import logicrepository.plugins.ptcaret.visitor.DumpVisitor;
import logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public class PTCARET_True extends PTCARET_Formula {

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	@Override
	public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public final String toString() {
		DumpVisitor visitor = new DumpVisitor();
		String formula = accept(visitor, null);
		return formula;
	}
}
