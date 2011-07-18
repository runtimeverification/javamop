package javamop.parser.ast.aspectj;

import java.util.*;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class MethodPattern extends FieldPattern {

	List<TypePattern> parameters;
	List<TypePattern> throwTypes = null;
	
	public MethodPattern(int line, int column, int modifiers, int not_modifiers, TypePattern type, TypePattern owner, String name, List<TypePattern> parameters, List<TypePattern> throwTypes){
		super(line, column, modifiers, not_modifiers, type, owner, name);
		this.parameters = parameters;
		this.throwTypes = throwTypes;
	}
	
	public List<TypePattern> getParameters() { return parameters; }
	public List<TypePattern> getThrows() { return throwTypes; }
	
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
	
}
