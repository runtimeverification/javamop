// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/*
 * Copyright (C) 2007 Julio Vilmar Gesser.
 * 
 * This file is part of Java 1.5 parser and Abstract Syntax Tree.
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 05/10/2006
 */
package javamop.parser.astex.mopspec;

import java.util.List;

import javamop.parser.astex.ExtNode;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

/**
 * @author Soha Hussein
 */
public final class ExtendedSpec extends ExtNode {
    
    private final String specName;
    
    private final boolean isParametric;
    
    List<String> parameters = null;
    
    public ExtendedSpec(int line, int column, String specName, boolean isParametric, List<String> parameters) {
        super(line, column);
        this.specName = specName;
        this.isParametric = isParametric;
        this.parameters = parameters;
    }
    
    public String getName() {
        return specName;
    }
    
    public boolean isParametric() {
        return isParametric;
    }
    
    public List<String> getParameters() {
        return parameters;
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
