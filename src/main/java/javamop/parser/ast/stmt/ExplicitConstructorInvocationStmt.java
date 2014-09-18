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
 * Created on 03/11/2006
 */
package javamop.parser.ast.stmt;

import java.util.List;

import javamop.parser.ast.expr.Expression;
import javamop.parser.ast.type.Type;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class ExplicitConstructorInvocationStmt extends Statement {

    private final List<Type> typeArgs;

    private final boolean isThis;

    private final Expression expr;

    private final List<Expression> args;

    public ExplicitConstructorInvocationStmt(int line, int column, List<Type> typeArgs, boolean isThis, Expression expr, List<Expression> args) {
        super(line, column);
        this.typeArgs = typeArgs;
        this.isThis = isThis;
        this.expr = expr;
        this.args = args;
    }

    public List<Type> getTypeArgs() {
        return typeArgs;
    }

    public boolean isThis() {
        return isThis;
    }

    public Expression getExpr() {
        return expr;
    }

    public List<Expression> getArgs() {
        return args;
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
