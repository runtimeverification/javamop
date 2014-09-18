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
package javamop.parser.ast.expr;

import java.util.List;

import javamop.parser.ast.type.Type;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class MethodCallExpr extends Expression {

    private final Expression scope;

    private final List<Type> typeArgs;

    private final String name;

    private final List<Expression> args;

    public MethodCallExpr(int line, int column, Expression scope, List<Type> typeArgs, String name, List<Expression> args) {
        super(line, column);
        this.scope = scope;
        this.typeArgs = typeArgs;
        this.name = name;
        this.args = args;
    }

    public Expression getScope() {
        return scope;
    }

    public List<Type> getTypeArgs() {
        return typeArgs;
    }

    public String getName() {
        return name;
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
