// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

public class WildcardParameter extends TypePattern {
    
    public WildcardParameter(int line, int column) {
        super(line, column, "..");
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
