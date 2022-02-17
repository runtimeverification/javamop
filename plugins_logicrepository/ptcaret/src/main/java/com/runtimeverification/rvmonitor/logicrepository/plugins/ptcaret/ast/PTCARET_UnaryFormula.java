package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public class PTCARET_UnaryFormula extends PTCARET_Formula {
	public static enum Operator {
		not, prev, eventually, always, ab_prev, ab_eventually, ab_always, at_begin, at_call, always_at_begin, always_at_call, always_at_begincall, eventually_at_begin, eventually_at_call, eventually_at_begincall,
	}

	private final PTCARET_Formula formula;
	private final Operator op;

	public PTCARET_UnaryFormula(PTCARET_Formula formula, Operator op) {
		this.formula = formula;
		this.op = op;
	}

	public PTCARET_Formula getFormula() {
		return formula;
	}

	public Operator getOp() {
		return op;
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
	public final String toString() {
		DumpVisitor visitor = new DumpVisitor();
		String formula = accept(visitor, null);
		return formula;
	}
}
