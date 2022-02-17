// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;


import java.util.List;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import javamop.parser.ast.visitor.BaseVisitor;
import javamop.parser.ast.visitor.MOPVoidVisitor;
import javamop.parser.astex.visitor.DumpVisitor;

public class CombinedPointCut extends PointCut {

    private final List<PointCut> pointcuts;

    public CombinedPointCut(TokenRange tokenRange, String type, List<PointCut> pointcuts) {
        super(tokenRange, type);

        this.pointcuts = pointcuts;
    }

    public List<PointCut> getPointcuts() {
        return pointcuts;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        if (v instanceof MOPVoidVisitor) {
            ((javamop.parser.astex.visitor.DumpVisitor)v).visit(this, (Void) arg);
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
