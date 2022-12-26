// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;

public class MethodPattern extends FieldPattern {
    
    private final List<TypePattern> parameters;
    private final List<TypePattern> throwTypes;
    
    public MethodPattern(TokenRange tokenRange, int modifiers, int not_modifiers, TypePattern type, TypePattern owner, String name, List<TypePattern> parameters, List<TypePattern> throwTypes){
        super(tokenRange, modifiers, not_modifiers, type, owner, name);
        this.parameters = parameters;
        this.throwTypes = throwTypes;
    }
    
    public List<TypePattern> getParameters() { return parameters; }

    public List<TypePattern> getThrows() { return throwTypes; }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
}
