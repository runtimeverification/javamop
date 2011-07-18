package javamop.logicpluginshells.javafsm.fsmparser.ast;

import java.util.*;


public class FSMInput extends Node {

	List<FSMItem> Items;
	List<FSMAlias> Aliases;

	
	public FSMInput (int line, int column, List<FSMItem> Items, List<FSMAlias> Aliases){
		super(line, column);
		this.Items = Items;
		this.Aliases = Aliases;
	}
	
	public List<FSMItem> getItems() { return Items; }
	
	public List<FSMAlias> getAliases() { return Aliases; }

	@Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
	
}
