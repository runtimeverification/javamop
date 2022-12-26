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

import java.util.Iterator;

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
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMonitorSpec;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.SpecModifierSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.typepattern.BaseTypePattern;

/**
 * @author Julio Vilmar Gesser
 */

public class DumpVisitor implements VoidVisitor<Object> {

    protected final SourcePrinter printer = new SourcePrinter();

    public String getSource() {
        return printer.getSource();
    }

    protected void printSpecModifiers(int modifiers) {
        if (SpecModifierSet.isAvoid(modifiers)) {
            printer.print("avoid ");
        }
        if (SpecModifierSet.isConnected(modifiers)) {
            printer.print("connected ");
        }
        if (SpecModifierSet.isDecentralized(modifiers)) {
            printer.print("decentralized ");
        }
        if (SpecModifierSet.isEnforce(modifiers)) {
            printer.print("enforce ");
        }
        if (SpecModifierSet.isFullBinding(modifiers)) {
            printer.print("full-binding ");
        }
        if (SpecModifierSet.isPerThread(modifiers)) {
            printer.print("perthread ");
        }
        if (SpecModifierSet.isSuffix(modifiers)) {
            printer.print("suffix ");
        }
        if (SpecModifierSet.isUnSync(modifiers)) {
            printer.print("unsynchronized ");
        }
    }

    protected void printSpecParameters(RVMParameters args, Object arg) {
        printer.print("(");
        if (args != null) {
            for (Iterator<RVMParameter> i = args.iterator(); i.hasNext();) {
                RVMParameter t = i.next();
                t.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(")");
    }

    @Override
    public void visit(Node n, Object arg) {
        throw new IllegalStateException(n.getClass().getName());
    }

    /* visit functions for RV Monitor components */

    @Override
    public void visit(RVMSpecFile f, Object arg) {
        if (f.getPakage() != null)
            f.getPakage().accept(this, arg);
        if (f.getImports() != null) {
            for (ImportDeclaration i : f.getImports()) {
                i.accept(this, arg);
            }
            printer.printLn();
        }
        if (f.getSpecs() != null) {
            for (RVMonitorSpec i : f.getSpecs()) {
                i.accept(this, arg);
                printer.printLn();
            }
        }

    }

    @Override
    public void visit(RVMonitorSpec s, Object arg) {
        printSpecModifiers(s.getModifiers());
        printer.print(s.getName());
        printSpecParameters(s.getParameters(), arg);
        if (s.getInMethod() != null) {
            printer.print(" within ");
            printer.print(s.getInMethod());
            // s.getInMethod().accept(this, arg);
        }
        printer.printLn(" {");
        printer.indent();

        if (s.getDeclarationsStr() != null) {
            printer.printLn(s.getDeclarationsStr());
        }

        if (s.getEvents() != null) {
            for (EventDefinition e : s.getEvents()) {
                e.accept(this, arg);
            }
        }

        if (s.getPropertiesAndHandlers() != null) {
            for (PropertyAndHandlers p : s.getPropertiesAndHandlers()) {
                p.accept(this, arg);
            }
        }

        printer.unindent();
        printer.printLn("}");
    }

    @Override
    public void visit(RVMParameter p, Object arg) {
        p.getType().accept(this, arg);
        printer.print(" " + p.getName());
    }

    @Override
    public void visit(EventDefinition e, Object arg) {
        printer.print("event " + e.getId() + " ");
        printSpecParameters(e.getParameters(), arg);
        printer.printLn(e.getAction());
        printer.printLn();
    }

    @Override
    public void visit(PropertyAndHandlers p, Object arg) {
        p.getProperty().accept(this, arg);
        printer.printLn();
        for (String event : p.getHandlers().keySet()) {
            String stmt = p.getHandlers().get(event);
            printer.printLn("@" + event);
            printer.indent();
            printer.printLn(stmt);
            printer.unindent();
            printer.printLn();
        }
    }

    @Override
    public void visit(Formula f, Object arg) {
        printer.print(f.getType() + ": " + f.getFormula());
    }

    /* visit functions for AspectJ components */

    @Override
    public void visit(BaseTypePattern p, Object arg) {
        printer.print(p.getOp());
    }

    /** visit functions for Java components */

    @Override
    public void visit(PackageDeclaration n, Object arg) {
        printer.print("package ");
        n.getName().accept(this, arg);
        printer.printLn(";");
        printer.printLn();
    }

    @Override
    public void visit(NameExpr n, Object arg) {
        printer.print(n.getName());
    }

    @Override
    public void visit(QualifiedNameExpr n, Object arg) {
        n.getQualifier().accept(this, arg);
        printer.print(".");
        printer.print(n.getName());
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        printer.print("import ");
        if (n.isStatic()) {
            printer.print("static ");
        }
        n.getName().accept(this, arg);
        if (n.isAsterisk()) {
            printer.print(".*");
        }
        printer.printLn(";");
    }
}
