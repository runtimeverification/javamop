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

package com.runtimeverification.rvmonitor.logicpluginshells.tfsm.visitor;

import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMAlias;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMInput;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMItem;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.FSMTransition;
import com.runtimeverification.rvmonitor.logicpluginshells.tfsm.ast.Node;

/**
 * @author Julio Vilmar Gesser
 */
public interface VoidVisitor<A> {

    public void visit(Node n, A arg);

    public void visit(FSMInput f, A arg);

    public void visit(FSMItem i, A arg);

    public void visit(FSMAlias a, A arg);

    public void visit(FSMTransition t, A arg);

}
