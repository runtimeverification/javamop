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
package javamop.parser.ast.body;

import java.util.List;

import javamop.parser.ast.TypeParameter;
import javamop.parser.ast.expr.AnnotationExpr;
import javamop.parser.ast.expr.NameExpr;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.ast.type.Type;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class MethodDeclaration extends BodyDeclaration {

    private final int modifiers;

    private final List<AnnotationExpr> annotations;

    private final List<TypeParameter> typeParameters;

    private final Type type;

    private final String name;

    private final List<Parameter> parameters;

    private final int arrayCount;

    private final List<NameExpr> throws_;

    private final BlockStmt body;

    public MethodDeclaration(int line, int column, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, Type type, String name, List<Parameter> parameters, int arrayCount, List<NameExpr> throws_, BlockStmt block) {
        super(line, column);
        this.modifiers = modifiers;
        this.annotations = annotations;
        this.typeParameters = typeParameters;
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.arrayCount = arrayCount;
        this.throws_ = throws_;
        this.body = block;
    }

    public int getModifiers() {
        return modifiers;
    }

    public List<AnnotationExpr> getAnnotations() {
        return annotations;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public int getArrayCount() {
        return arrayCount;
    }

    public List<NameExpr> getThrows() {
        return throws_;
    }

    public BlockStmt getBody() {
        return body;
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
