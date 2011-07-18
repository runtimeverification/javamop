package javamop.parser.ast.mopspec;

import java.util.*;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class CombinedProperty extends Property {
	
	List<Property> properties;

	public CombinedProperty(int line, int column, String type, List<Property> properties) {
		super(line, column, type);
		this.properties = properties;
	}
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

}
