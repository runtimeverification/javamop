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
package com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.Node;

/**
 * @author Julio Vilmar Gesser
 */
public interface GenericVisitor<R, A> {

    public R visit(Node n, A arg);

    public R visit(FSMInput f, A arg);

    public R visit(FSMItem i, A arg);

    public R visit(FSMAlias a, A arg);

    public R visit(FSMTransition t, A arg);

}
