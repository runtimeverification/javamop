package logicrepository.plugins.pda.visitor;

import logicrepository.plugins.pda.ast.Event;
import logicrepository.plugins.pda.ast.PDA;
import logicrepository.plugins.pda.ast.StackSymbol;
import logicrepository.plugins.pda.ast.State;

public interface VoidVisitor<A> {

	public void visit(PDA n, A arg);

	public void visit(State n, A arg);

	public void visit(StackSymbol n, A arg);

	public void visit(Event n, A arg);

}