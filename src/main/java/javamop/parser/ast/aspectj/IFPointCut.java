// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.expr.Expression;

public class IFPointCut extends PointCut {
    
    private final Expression expr;
    
    public IFPointCut(TokenRange tokenRange, String type, Expression expr) {
        super(tokenRange, type);
        this.expr = expr;
    }
    
    public Expression getExpression() { return expr; }
    
}
