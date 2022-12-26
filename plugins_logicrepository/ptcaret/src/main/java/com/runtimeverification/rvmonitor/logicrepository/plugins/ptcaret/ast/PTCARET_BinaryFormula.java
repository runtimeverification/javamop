package com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.ast;

import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ptcaret.visitor.VoidVisitor;

public class PTCARET_BinaryFormula extends PTCARET_Formula {
	public static enum Operator {
		iff, implies, or, xor, and, since_at_b, since_at_c, since_at_bc, ab_since, since,
	}

	private final PTCARET_Formula left;
	private final PTCARET_Formula right;
	private final Operator op;

	public PTCARET_BinaryFormula(PTCARET_Formula f1, PTCARET_Formula f2, Operator op) {
		this.left = f1;
		this.right = f2;
		this.op = op;
	}

	public PTCARET_Formula getLeft() {
		return left;
	}

	public PTCARET_Formula getRight() {
		return right;
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
