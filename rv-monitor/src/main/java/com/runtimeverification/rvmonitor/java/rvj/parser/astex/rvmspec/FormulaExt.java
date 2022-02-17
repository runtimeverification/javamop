package com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec;

import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor.VoidVisitor;

public class FormulaExt extends PropertyExt {

    private final String formula;

    public FormulaExt(int line, int column, String type, String formula,
            String propertyName) {
        super(line, column, type, propertyName);
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
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
