// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

import java.util.List;

public class MethodPattern extends FieldPattern {
    
    private final List<TypePattern> parameters;
    private final List<TypePattern> throwTypes;
    
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
