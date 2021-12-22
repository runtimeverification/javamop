// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import com.github.javaparser.TokenRange;

public class FormulaExt extends PropertyExt {
    
    private final String formula;
    
    public FormulaExt(TokenRange tokenRange, String type, String formula, String propertyName) {
        super(tokenRange, type, propertyName);
        this.formula = formula;
    }
    
    public String getFormula() { return formula; }

}
