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

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.ImportDeclaration;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.RVMSpecFileExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.EventDefinitionExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ExtendedSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.FormulaExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.HandlerExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.PropertyAndHandlersExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.RVMonitorSpecExt;
import com.runtimeverification.rvmonitor.java.rvj.parser.astex.rvmspec.ReferenceSpec;

/**
 * @author Julio Vilmar Gesser
 */

public final class DumpVisitor
extends
com.runtimeverification.rvmonitor.java.rvj.parser.ast.visitor.DumpVisitor
implements VoidVisitor<Object> {

    // All extended componenets

    // - RV Monitor components

    public void visit(RVMSpecFileExt f, Object arg) {
        if (f.getPakage() != null)
            f.getPakage().accept(this, arg);
        if (f.getImports() != null) {
            for (ImportDeclaration i : f.getImports()) {
                i.accept(this, arg);
            }
            printer.printLn();
        }
        if (f.getSpecs() != null) {
            for (RVMonitorSpecExt i : f.getSpecs()) {
                i.accept(this, arg);
                printer.printLn();
            }
        }
    }

    // soha: printing out references to other spec
    @Override
    public void visit(ReferenceSpec r, Object arg) {
        if (r.getSpecName() != null)
            if (r.getReferenceElement() != null)
                printer.print(r.getSpecName() + "." + r.getReferenceElement());
            else
                printer.print(r.getSpecName());
        else if (r.getReferenceElement() != null)
            printer.print(r.getReferenceElement());

    }

    @Override
    public void visit(RVMonitorSpecExt s, Object arg) {
        if (s.isPublic())
            printer.print("public ");
        printSpecModifiers(s.getModifiers());
        printer.print(s.getName());
        printSpecParameters(s.getParameters(), arg);
        if (s.getInMethod() != null) {
            printer.print(" within ");
            printer.print(s.getInMethod());
            // s.getInMethod().accept(this, arg);
        }
        if (s.hasExtend()) {
            printer.print(" includes ");
            int size = 1;
            for (ExtendedSpec e : s.getExtendedSpec()) {
                e.accept(this, arg);
                if (size != s.getExtendedSpec().size())
                    printer.print(",");
                size++;
            }
        }

        printer.printLn(" {");
        printer.indent();

        if (s.getDeclarationsStr() != null) {
            printer.printLn(s.getDeclarationsStr());
        }

        if (s.getEvents() != null) {
            for (EventDefinitionExt e : s.getEvents()) {
                e.accept(this, arg);
            }
        }

        if (s.getPropertiesAndHandlers() != null) {
            for (PropertyAndHandlersExt p : s.getPropertiesAndHandlers()) {
                p.accept(this, arg);
            }
        }

        printer.unindent();
        printer.printLn("}");
    }

    @Override
    public void visit(EventDefinitionExt e, Object arg) {
        printer.print("event " + e.getId() + " ");
        printSpecParameters(e.getParameters(), arg);
        printer.printLn(e.getAction());
        printer.printLn();
    }

    @Override
    public void visit(PropertyAndHandlersExt p, Object arg) {
        if (p.getProperty() != null)
            p.getProperty().accept(this, arg);
        printer.printLn();
        for (String event : p.getHandlers().keySet()) {
            for (HandlerExt h : p.getHandlerList()) { // i need to remove that
                // later, i'm using it
                // now to just make sure
                // that things are
                // parsed correctly.
                // Soha.
                if (h.getState() == event) { // Soha: printing out the new
                    // syntax of the handler and
                    // property
                    if ((h.getReferenceSpec().getSpecName() != null)
                            || (h.getReferenceSpec().getReferenceElement() != null))
                        printer.print("@");
                    h.accept(this, arg);
                }
            }
            String stmt = p.getHandlers().get(event);
            printer.printLn("@" + event);
            printer.indent();
            printer.printLn(stmt);
            printer.unindent();
            printer.printLn();
        }
    }

    @Override
    public void visit(FormulaExt f, Object arg) {
        printer.print(f.getType() + " " + f.getName() + ": " + f.getFormula());
    }

    public void visit(HandlerExt h, Object arg) {
        h.getReferenceSpec().accept(this, arg);
    }

    @Override
    public void visit(ExtendedSpec extendedSpec, Object arg) {
        printer.print(" " + extendedSpec.getName());
        if (extendedSpec.isParametric()) {
            printer.print("(");
            int size = 1;
            for (String extendedParameters : extendedSpec.getParameters()) {
                printer.print(extendedParameters);
                if (size != extendedSpec.getParameters().size())
                    printer.print(",");
                size++;
            }

            printer.print(")");
        }
    }
}
