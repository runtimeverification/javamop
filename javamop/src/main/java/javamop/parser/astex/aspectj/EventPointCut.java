// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.aspectj;

import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.mopspec.ReferenceSpec;

public class EventPointCut extends PointCut {
    
    private final ReferenceSpec r;
    private final List<String> parameterNames;
    
    public EventPointCut(TokenRange tokenRange, String type, String specName, String referenceElement, List<String> parameterNames) {
        super(tokenRange, type);
        this.parameterNames = parameterNames;
        this.r = new ReferenceSpec(tokenRange, specName, referenceElement, "event");
    }
    
    public ReferenceSpec getReferenceSpec() {
        return r;
    }
    
    public List<String> getParameters() {
        return parameterNames;
    }

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
