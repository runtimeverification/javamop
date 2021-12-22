// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import com.github.javaparser.TokenRange;
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
}
