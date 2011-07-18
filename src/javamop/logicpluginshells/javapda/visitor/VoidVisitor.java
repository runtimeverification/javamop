package javamop.logicpluginshells.javapda.visitor;

import javamop.logicpluginshells.javapda.ast.Event;
import javamop.logicpluginshells.javapda.ast.PDA;
import javamop.logicpluginshells.javapda.ast.StackSymbol;
import javamop.logicpluginshells.javapda.ast.State;


public interface VoidVisitor<A> {

	public void visit(PDA n, A arg);

	public void visit(State n, A arg);

	public void visit(StackSymbol n, A arg);

	public void visit(Event n, A arg);

}