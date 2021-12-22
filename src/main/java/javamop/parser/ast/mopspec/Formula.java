// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.mopspec;

import com.github.javaparser.TokenRange;

public class Formula extends Property {
    
    private final String formula;
    
    public Formula(TokenRange tokenRange, String type, String formula) {
        super(tokenRange, type);
        this.formula = formula;
    }
    
    public String getFormula() { return formula; }
    
}
