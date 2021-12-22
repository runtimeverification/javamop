// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import java.util.List;

import com.github.javaparser.TokenRange;

public class ArgsPointCut extends PointCut {
    
    private final List<TypePattern> args;
    
    public ArgsPointCut(TokenRange tokenRange, String type, List<TypePattern> args){
        super(tokenRange, type);
        this.args = args;
    }
    
    public List<TypePattern> getArgs() { return args; }
    
}
