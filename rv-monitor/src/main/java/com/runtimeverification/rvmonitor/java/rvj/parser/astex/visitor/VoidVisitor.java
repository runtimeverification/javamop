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

package com.runtimeverification.rvmonitor.java.rvj.parser.astex.visitor;

import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.EventDefinitionExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ExtendedSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.FormulaExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyAndHandlersExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.RVMonitorSpecExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ReferenceSpec;

/**
 * @author Julio Vilmar Gesser
 */
public interface VoidVisitor<A>
extends
com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.VoidVisitor<A> {

    // All extended componenets

    // - RV Monitor components

    public void visit(ReferenceSpec r, A arg);

    public void visit(RVMonitorSpecExt s, A arg);

    public void visit(EventDefinitionExt e, A arg);

    public void visit(PropertyAndHandlersExt p, A arg);

    public void visit(FormulaExt f, A arg);

    public void visit(ExtendedSpec extendedSpec, A arg);
}
