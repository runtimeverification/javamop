package com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec;

import com.runtimeverification.rvmonitor.java.rvj.parser.astex.ExtNode;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;

/**
 * @author Soha Hussein
 */
public class ReferenceSpec extends ExtNode {

    private final String specName;

    private final String referenceElement;

    private final String elementType;

    public ReferenceSpec(int line, int column, String specName,
            String referenceElement, String elementType) {
        super(line, column);
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
        return this.specName.equals(r.getSpecName())
                && this.referenceElement.equals(r.getReferenceElement());
    }

    @Override
    public int hashCode() {
        return (this.specName + this.referenceElement).hashCode();
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
