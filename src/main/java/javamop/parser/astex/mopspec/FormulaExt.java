// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.mopspec;

import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

public class FormulaExt extends PropertyExt {
    
    private final String formula;
    
    public FormulaExt(int line, int column, String type, String formula, String propertyName) {
        super(line, column, type, propertyName);
        this.formula = formula;
    }
    
    public String getFormula() { return formula; }
    
    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }
    
    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
    
}
