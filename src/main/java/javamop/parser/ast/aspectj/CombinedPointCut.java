// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.aspectj;


import java.util.List;

import com.github.javaparser.TokenRange;

public class CombinedPointCut extends PointCut {

    private final List<PointCut> pointcuts;

    public CombinedPointCut(TokenRange tokenRange, String type, List<PointCut> pointcuts) {
        super(tokenRange, type);

        this.pointcuts = pointcuts;
    }

    public List<PointCut> getPointcuts() {
        return pointcuts;
    }
}
