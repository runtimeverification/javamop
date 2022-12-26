// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.ExtNode;

/**
 * @author Soha Hussein
 */
public class ReferenceSpec extends ExtNode {
    
    private final String specName;
    private final String referenceElement;
    private final String elementType;
    
    public ReferenceSpec(TokenRange tokenRange, String specName, String referenceElement, String elementType) {
        super(tokenRange);
        this.specName = specName;
        this.referenceElement = referenceElement;
        this.elementType = elementType;
    }
    
    public String getSpecName() {
        return specName;
    }
    
    public String getReferenceElement() {
        return referenceElement;
    }
    
    public String getElementType() {
        return elementType;
    }
    
    public boolean equals(ReferenceSpec r) {
        return this.specName.equals(r.getSpecName()) && this.referenceElement.equals(r.getReferenceElement());
    }

    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof javamop.parser.ast.visitor.MOPVoidVisitor) {
            ((MOPVoidVisitor)v).visit(this, arg);
        }
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof BaseVisitor) {
            return ((BaseVisitor<R, A>) v).visit(this, arg);
        } else {
            return null;
        }
    }
}
