package javamop.parser.ast.aspectj;

import java.util.*;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class CombinedTypePattern extends TypePattern {
	
	List<TypePattern> sub_types;
	
	public CombinedTypePattern(int line, int column, String op, List<TypePattern> sub_types){
		super(line, column, op);
		this.sub_types = sub_types;
	}
	
	public List<TypePattern> getSubTypes() { return sub_types; }
    
	@Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }	

}
