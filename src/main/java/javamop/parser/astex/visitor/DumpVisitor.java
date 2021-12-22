// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/*
 * Copyright (C) 2008 Feng Chen.
 * 
 * This file is part of JavaMOP parser.
 *
 * JavaMOP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaMOP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaMOP.  If not, see <http://www.gnu.org/licenses/>.
 */

package javamop.parser.astex.visitor;

import java.util.Iterator;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.MOPPrinter;
import com.github.javaparser.printer.configuration.PrinterConfiguration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.mopspec.SpecModifierSet;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.aspectj.EventPointCut;
import javamop.parser.astex.aspectj.HandlerPointCut;
import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.ExtendedSpec;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.HandlerExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.astex.mopspec.ReferenceSpec;

/**
 * @author Julio Vilmar Gesser
 */

public class DumpVisitor extends DefaultPrettyPrinterVisitor implements MOPVoidVisitor<Object> {

	public DumpVisitor(PrinterConfiguration configuration) {
		super(configuration, new MOPPrinter());
	}

	// - JavaMOP components

	public void visit(MOPSpecFile f, Object arg) {
		if (f.getPakage() != null)
			f.getPakage().accept((VoidVisitor)this, arg);
		if (f.getImports() != null) {
			for (ImportDeclaration i : f.getImports()) {
				i.accept((VoidVisitor)this, arg);
			}
			printer.println();
		}
		if (f.getSpecs() != null) {
			for (JavaMOPSpec i : f.getSpecs()) {
				i.accept((VoidVisitor)this, arg);
				printer.println();
			}
		}

	}

	public void visit(JavaMOPSpec s, Object arg) {
		printSpecModifiers(s.getModifiers());
		printer.print(s.getName());
		printSpecParameters(s.getParameters(), arg);
		if (s.getInMethod() != null) {
			printer.print(" within ");
			printer.print(s.getInMethod());
			// s.getInMethod().accept(this, arg);
		}
		printer.println(" {");
		printer.indent();

		if (s.getDeclarations() != null) {
			printMembers(s.getDeclarations(), (Void)arg);
		}

		if (s.getEvents() != null) {
			for (EventDefinition e : s.getEvents()) {
				e.accept((VoidVisitor)this, arg);
			}
		}

		if (s.getPropertiesAndHandlers() != null) {
			for (PropertyAndHandlers p : s.getPropertiesAndHandlers()) {
				p.accept((VoidVisitor)this, arg);
			}
		}

		printer.unindent();
		printer.println("}");
	}

	public void visit(MOPParameter p, Object arg) {
		p.getType().accept((VoidVisitor)this, arg);
		printer.print(" " + p.getName());
	}

	public void visit(EventDefinition e, Object arg) {
		if (e.isCreationEvent()) {
			printer.print("creation ");
		} else if (e.isStaticEvent()) {
			printer.print("static ");
		}
		printer.print("event " + e.getId() + " " + e.getPos());
		printSpecParameters(e.getParameters(), arg);
		if (e.hasReturning()) {
			printer.print("returning");
			if (e.getRetVal() != null)
				printSpecParameters(e.getRetVal(), arg);
			printer.print(" ");
		}
		if (e.hasThrowing()) {
			printer.print("throwing");
			if (e.getThrowVal() != null)
				printSpecParameters(e.getThrowVal(), arg);
			printer.print(" ");
		}
		printer.print(":");
		// e.getPointCut().accept(this, arg);
		printer.print(e.getPointCutString());
		if (e.getAction() != null) {
			e.getAction().accept((VoidVisitor)this, arg);
		}
		printer.println();
	}

	public void visit(PropertyAndHandlers p, Object arg) {
		p.getProperty().accept((VoidVisitor)this, arg);
		printer.println();
		for (String event : p.getHandlers().keySet()) {
			BlockStmt stmt = p.getHandlers().get(event);
			printer.println("@" + event);
			printer.indent();
			stmt.accept((VoidVisitor)this, arg);
			printer.unindent();
			printer.println();
		}
	}

	public void visit(Formula f, Object arg) {
		printer.print(f.getType() + ": " + f.getFormula());
	}
	
	// All extended componenets

	public void visit(MOPSpecFileExt f, Object arg) {
		if (f.getPakage() != null)
			f.getPakage().accept((VoidVisitor)this, arg);
		if (f.getImports() != null) {
			for (ImportDeclaration i : f.getImports()) {
				i.accept((VoidVisitor)this, arg);
			}
			printer.println();
		}
		if (f.getSpecs() != null) {
			for (JavaMOPSpecExt i : f.getSpecs()) {
				i.accept((VoidVisitor)this, arg);
				printer.println();
			}
		}
	}

	// soha: printing out references to other spec
	public void visit(ReferenceSpec r, Object arg) {
		if (r.getSpecName() != null)
			if (r.getReferenceElement() != null)
				printer.print(r.getSpecName() + "." + r.getReferenceElement());
			else
				printer.print(r.getSpecName());
		else if (r.getReferenceElement() != null)
			printer.print(r.getReferenceElement());

	}

	public void visit(JavaMOPSpecExt s, Object arg) {
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
				e.accept((VoidVisitor)this, arg);
				if (size != s.getExtendedSpec().size())
					printer.print(",");
				size++;
			}
		}

		printer.println(" {");
		printer.indent();

		if (s.getDeclarations() != null) {
			printMembers(s.getDeclarations(), (Void)arg);
		}

		if (s.getEvents() != null) {
			for (EventDefinitionExt e : s.getEvents()) {
				e.accept((VoidVisitor)this, arg);
			}
		}

		if (s.getPropertiesAndHandlers() != null) {
			for (PropertyAndHandlersExt p : s.getPropertiesAndHandlers()) {
				p.accept((VoidVisitor)this, arg);
			}
		}

		printer.unindent();
		printer.println("}");
	}

	public void visit(EventDefinitionExt e, Object arg) {
		if (e.isAbstract())
			printer.print("abstract "); // Soha:printing out abstract keyword

		printer.print("event " + e.getId() + " " + e.getPos());
		printSpecParameters(e.getParameters(), arg);
		if (e.hasReturning()) {
			printer.print("returning");
			if (e.getRetVal() != null)
				printSpecParameters(e.getRetVal(), arg);
			printer.print(" ");
		}
		if (e.hasThrowing()) {
			printer.print("throwing");
			if (e.getThrowVal() != null)
				printSpecParameters(e.getThrowVal(), arg);
			printer.print(" ");
		}
		if (!e.isAbstract()) {
			printer.print(":");

			// e.getPointCut().accept(this, arg);
			printer.print(e.getPointCutString());
			if (e.getAction() != null) {
				e.getAction().accept((VoidVisitor)this, arg);
			}
		} else
			printer.print(";");

		printer.println();
	}

	public void visit(PropertyAndHandlersExt p, Object arg) {
		if (p.getProperty() != null)
			p.getProperty().accept((VoidVisitor)this, arg);
		printer.println();
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
					if ((h.getReferenceSpec().getSpecName() != null) || (h.getReferenceSpec().getReferenceElement() != null))
						printer.print("@");
					h.accept((VoidVisitor)this, arg);
				}
			}
			BlockStmt stmt = p.getHandlers().get(event);
			printer.println("@" + event);
			printer.indent();
			stmt.accept((VoidVisitor)this, arg);
			printer.unindent();
			printer.println();
		}
	}
	
	public void visit(FormulaExt f, Object arg) {
		printer.print(f.getType()+ " "+ f.getName() + ": " + f.getFormula());
	}

	public void visit(HandlerExt h, Object arg) {
		h.getReferenceSpec().accept((VoidVisitor)this, arg);
	}

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

	// - AspectJ components --------------------

	public void visit(EventPointCut p, Object arg) {
		printer.print("event" + "(");
		p.getReferenceSpec().accept((VoidVisitor)this, arg);
		printer.print(")");
	}

	public void visit(HandlerPointCut p, Object arg) {
		printer.print("handler" + "(");
		p.getReferenceSpec().accept((VoidVisitor)this, arg);
		printer.print("@" + p.getState());
		printer.print(")");
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

	protected void printSpecParameters(MOPParameters args, Object arg) {
		printer.print("(");
		if (args != null) {
			for (Iterator<MOPParameter> i = args.iterator(); i.hasNext();) {
				MOPParameter t = i.next();
				t.accept((VoidVisitor)this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");
	}
}
