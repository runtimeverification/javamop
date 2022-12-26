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
package com.runtimeverification.rvmonitor.java.rvj.parser.ast;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.DumpVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public abstract class Node {

    protected final int beginLine;

    protected final int beginColumn;

    protected final int endLine;

    protected final int endColumn;

    /**
     * This attribute can store additional information from semantic analysis.
     */
    protected Object data;

    public Node(int line, int column) {
        this.beginLine = line;
        this.beginColumn = column;
        this.endLine = line;
        this.endColumn = column;
    }

    public Node(int beginLine, int beginColumn, int endLine, int endColumn) {
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    /**
     * Use this to retrieve additional information associated to this node.
     */
    public Object getData() {
        return data;
    }

    /**
     * Use this to store additional information to this node.
     */
    public void setData(Object data) {
        this.data = data;
    }

    public final int getBeginLine() {
        return beginLine;
    }

    public final int getBeginColumn() {
        return beginColumn;
    }

    public final int getEndLine() {
        return endLine;
    }

    public final int getEndColumn() {
        return endColumn;
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
