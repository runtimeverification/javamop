package javamop.parser.ast.aspectj;

import javamop.parser.ast.*;

public abstract class TypePattern extends Node {

	String op;
	
	public TypePattern(int line, int column, String op) {
		super(line, column);
		this.op = op;
	}
	
	public String getOp() { return op; }
}
