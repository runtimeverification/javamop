/*
 * Copyright (C) 2008 Feng Chen.
 *
 * This file is part of RV Monitor parser.
 *
 * RV Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RV Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RV Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.ImportDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.Node;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.PackageDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.RVMSpecFile;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.expr.NameExpr;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.expr.QualifiedNameExpr;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.EventDefinition;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.Formula;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.PropertyAndHandlers;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern.BaseTypePattern;

/**
 * @author Julio Vilmar Gesser
 */
public interface VoidVisitor<A> {

    public void visit(Node n, A arg);

    // - RV Monitor components

    public void visit(RVMSpecFile f, A arg);

    public void visit(RVMonitorSpec s, A arg);

    public void visit(RVMParameter p, A arg);

    public void visit(EventDefinition e, A arg);

    public void visit(PropertyAndHandlers p, A arg);

    public void visit(Formula f, A arg);

    // - AspectJ components --------------------

    public void visit(BaseTypePattern p, A arg);

    // - Compilation Unit ----------------------------------

    public void visit(PackageDeclaration n, A arg);

    public void visit(ImportDeclaration n, A arg);

    // - Expression ----------------------------------------

    public void visit(NameExpr n, A arg);

    public void visit(QualifiedNameExpr n, A arg);
}
