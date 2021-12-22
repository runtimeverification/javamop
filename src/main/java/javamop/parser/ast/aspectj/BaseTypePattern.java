// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class BaseTypePattern extends TypePattern {
    
    public BaseTypePattern(TokenRange tokenRange, String op) {
        super(tokenRange, op);
    }
    
    public <A> void accept(VoidVisitor<A> v, A arg) {
        NodeList nodeList = new NodeList();
        nodeList.add(this);
        v.visit(nodeList, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        NodeList nodeList = new NodeList();
        nodeList.add(this);
        return v.visit(nodeList, arg);
    }

}
