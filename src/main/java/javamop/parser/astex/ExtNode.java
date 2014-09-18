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
package javamop.parser.astex;

import javamop.parser.ast.Node;
import javamop.parser.astex.visitor.DumpVisitor;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public abstract class ExtNode extends Node{

    public ExtNode(int line, int column) {
    	super(line, column);
    }

    public ExtNode(int beginLine, int beginColumn, int endLine, int endColumn) {
    	super(beginLine, beginColumn, endLine, endColumn);
    }


    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public String toString() {
        DumpVisitor visitor = new DumpVisitor();
        accept(visitor, null);
        return visitor.getSource();
    }

}
