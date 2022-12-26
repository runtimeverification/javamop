// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import javamop.parser.ast.visitor.PointcutVisitor;
import javamop.parser.astex.visitor.DumpVisitor;

public abstract class PointCut extends Node {
    
    private final String type;
    
    public PointCut(TokenRange tokenRange, String type){
        super(tokenRange);
        this.type = type;
    }
    
    public String getType() { return type; }

    public <R, A> R accept(PointcutVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    public String toRVString() {
        DumpVisitor visitor = new DumpVisitor(new DefaultPrinterConfiguration());
        accept(visitor, null);
        return visitor.getSource();
    }
}
