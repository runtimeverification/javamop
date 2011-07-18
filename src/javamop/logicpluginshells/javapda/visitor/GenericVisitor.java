package javamop.logicpluginshells.javapda.visitor;

import javamop.logicpluginshells.javapda.ast.Event;
import javamop.logicpluginshells.javapda.ast.PDA;
import javamop.logicpluginshells.javapda.ast.StackSymbol;
import javamop.logicpluginshells.javapda.ast.State;


public interface GenericVisitor<R, A> {

	public R visit(PDA n, A arg);

	public R visit(State n, A arg);

	public R visit(StackSymbol n, A arg);

	public R visit(Event n, A arg);

}