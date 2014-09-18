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
import javamop.parser.ast.type.ClassOrInterfaceType;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class ClassOrInterfaceDeclaration extends TypeDeclaration {

    private final List<AnnotationExpr> annotations;

    private final boolean isInterface;

    private final List<TypeParameter> typeParameters;

    private final List<ClassOrInterfaceType> extendsList;

    private final List<ClassOrInterfaceType> implementsList;

    public ClassOrInterfaceDeclaration(int line, int column, int modifiers, List<AnnotationExpr> annotations, boolean isInterface, String name, List<TypeParameter> typeParameters, List<ClassOrInterfaceType> extendsList, List<ClassOrInterfaceType> implementsList, List<BodyDeclaration> members) {
        super(line, column, name, modifiers, members);
        this.annotations = annotations;
        this.isInterface = isInterface;
        this.typeParameters = typeParameters;
        this.extendsList = extendsList;
        this.implementsList = implementsList;
    }

    public List<AnnotationExpr> getAnnotations() {
        return annotations;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<ClassOrInterfaceType> getExtends() {
        return extendsList;
    }

    public List<ClassOrInterfaceType> getImplements() {
        return implementsList;
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
