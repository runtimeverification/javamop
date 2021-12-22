// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;

import java.util.List;

import com.github.javaparser.TokenRange;

public class IDPointCut extends PointCut {
    
    private final List<TypePattern> args;
    private final String id;
    
    public IDPointCut(TokenRange tokenRange, String id, List<TypePattern> args){
        super(tokenRange, "id");
        this.args = args;
        this.id = id;
    }
    
    public List<TypePattern> getArgs() { return args; }
    
    public String getId() { return id; }
}
