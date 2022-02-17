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

package javamop.parser.ast.visitor;

import java.util.Iterator;
import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.MOPPrinter;
import com.github.javaparser.printer.SourcePrinter;
import com.github.javaparser.printer.configuration.PrinterConfiguration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.body.ModifierSet;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.mopspec.SpecModifierSet;

/**
 * @author Julio Vilmar Gesser
 */

public class DumpVisitor extends DefaultPrettyPrinterVisitor implements javamop.parser.ast.visitor.MOPVoidVisitor<Void> {

	protected final SourcePrinter printer;

	public DumpVisitor(PrinterConfiguration configuration, SourcePrinter printer) {
		super(configuration, printer);
		this.printer = printer;
	}

	public String getSource() {
		return printer.toString();
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

	protected void printModifiers(int modifiers) {
		if (ModifierSet.isPrivate(modifiers)) {
			printer.print("private ");
		}
		if (ModifierSet.isProtected(modifiers)) {
			printer.print("protected ");
		}
		if (ModifierSet.isPublic(modifiers)) {
			printer.print("public ");
		}
		if (ModifierSet.isAbstract(modifiers)) {
			printer.print("abstract ");
		}
		if (ModifierSet.isFinal(modifiers)) {
			printer.print("final ");
		}
		if (ModifierSet.isNative(modifiers)) {
			printer.print("native ");
		}
		if (ModifierSet.isStatic(modifiers)) {
			printer.print("static ");
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			printer.print("strictfp ");
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			printer.print("synchronized ");
		}
		if (ModifierSet.isTransient(modifiers)) {
			printer.print("transient ");
		}
		if (ModifierSet.isVolatile(modifiers)) {
			printer.print("volatile ");
		}
	}

	protected void printNotModifiers(int modifiers) {
		if (ModifierSet.isPrivate(modifiers)) {
			printer.print("!private ");
		}
		if (ModifierSet.isProtected(modifiers)) {
			printer.print("!protected ");
		}
		if (ModifierSet.isPublic(modifiers)) {
			printer.print("!public ");
		}
		if (ModifierSet.isAbstract(modifiers)) {
			printer.print("!abstract ");
		}
		if (ModifierSet.isFinal(modifiers)) {
			printer.print("!final ");
		}
		if (ModifierSet.isNative(modifiers)) {
			printer.print("!native ");
		}
		if (ModifierSet.isStatic(modifiers)) {
			printer.print("!static ");
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			printer.print("!strictfp ");
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			printer.print("!synchronized ");
		}
		if (ModifierSet.isTransient(modifiers)) {
			printer.print("!transient ");
		}
		if (ModifierSet.isVolatile(modifiers)) {
			printer.print("!volatile ");
		}
	}

	protected void printMembers(NodeList<BodyDeclaration<?>> members, Void arg) {
		for (BodyDeclaration member : members) {
			printer.println();
			member.accept(this, arg);
			printer.println();
		}
	}

	protected void printMemberAnnotations(List<AnnotationExpr> annotations, Void arg) {
		if (annotations != null) {
			for (AnnotationExpr a : annotations) {
				a.accept(this, arg);
				printer.println();
			}
		}
	}

	protected void printAnnotations(List<AnnotationExpr> annotations, Void arg) {
		if (annotations != null) {
			for (AnnotationExpr a : annotations) {
				a.accept(this, arg);
				printer.print(" ");
			}
		}
	}

	protected void printTypeArgs(List<Type> args, Void arg) {
		if (args != null) {
			printer.print("<");
			for (Iterator<Type> i = args.iterator(); i.hasNext();) {
				Type t = i.next();
				t.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(">");
		}
	}

	protected void printTypeParameters(List<TypeParameter> args, Void arg) {
		if (args != null) {
			printer.print("<");
			for (Iterator<TypeParameter> i = args.iterator(); i.hasNext();) {
				TypeParameter t = i.next();
				t.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(">");
		}
	}

	protected void printSpecParameters(MOPParameters args, Void arg) {
		printer.print("(");
		if (args != null) {
			for (Iterator<MOPParameter> i = args.iterator(); i.hasNext();) {
				MOPParameter t = i.next();
				t.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");
	}

	public void visit(Node n, Void arg) {
		throw new IllegalStateException(n.getClass().getName());
	}

	/* visit functions for JavaMOP components */

	public void visit(MOPSpecFile f, Void arg) {
		if (f.getPakage() != null)
			f.getPakage().accept(this, arg);
		if (f.getImports() != null) {
			for (ImportDeclaration i : f.getImports()) {
				i.accept(this, arg);
			}
			printer.println();
		}
		if (f.getSpecs() != null) {
			for (JavaMOPSpec i : f.getSpecs()) {
				i.accept(this, arg);
				printer.println();
			}
		}

	}

	public void visit(JavaMOPSpec s, Void arg) {
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
			printMembers(s.getDeclarations(), arg);
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
		printer.println("}");
	}

	public void visit(MOPParameter p, Void arg) {
		p.getType().accept(this, arg);
		printer.print(" " + p.getName());
	}

	public void visit(EventDefinition e, Void arg) {
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
			e.getAction().accept(this, arg);
		}
		printer.println();
	}

	public void visit(PropertyAndHandlers p, Void arg) {
		p.getProperty().accept(this, arg);
		printer.println();
		for (String event : p.getHandlers().keySet()) {
			BlockStmt stmt = p.getHandlers().get(event);
			printer.println("@" + event);
			printer.indent();
			stmt.accept(this, arg);
			printer.unindent();
			printer.println();
		}
	}

	public void visit(Formula f, Void arg) {
		printer.print(f.getType() + ": " + f.getFormula());
	}

	/* visit functions for AspectJ components */

	protected void printParameterTypes(List<TypePattern> args, Void arg) {
		printer.print("(");
		printTypePatterns(args, arg);
		printer.print(")");
	}

	protected void printTypePatterns(List<TypePattern> args, Void arg) {
		if (args != null) {
			for (Iterator<TypePattern> i = args.iterator(); i.hasNext();) {
				TypePattern t = i.next();
				t.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
	}

	public void visit(WildcardParameter w, Void arg) {
		printer.print("..");
	}

	public void visit(ArgsPointCut p, Void arg) {
		printer.print("args");
		printParameterTypes(p.getArgs(), arg);
	}

	public void visit(CombinedPointCut p, Void arg) {
		for (Iterator<PointCut> i = p.getPointcuts().iterator(); i.hasNext();) {
			PointCut subP = i.next();
			if ((p.getPointcuts().size() > 1) && (subP instanceof CombinedPointCut)) {
				printer.print("(");
			}
			subP.accept(this, arg);
			if ((p.getPointcuts().size() > 1) && (subP instanceof CombinedPointCut)) {
				printer.print(")");
			}
			if (i.hasNext()) {
				printer.print(" " + p.getType() + " ");
			}
		}
	}

	public void visit(ThreadPointCut p, Void arg) {
		printer.print("thread(" + p.getId() + ")");
	}

	public void visit(ThreadNamePointCut p, Void arg) {
		printer.print("threadName(" + p.getId() + ")");
	}

	public void visit(ThreadBlockedPointCut p, Void arg) {
		printer.print("threadBlocked(" + p.getId() + ")");
	}

	public void visit(EndProgramPointCut p, Void arg) {
		printer.print("endProgram()");
	}

	public void visit(EndThreadPointCut p, Void arg) {
		printer.print("endThread(");
		printer.print(")");
	}

	public void visit(StartThreadPointCut p, Void arg) {
		printer.print("startThread(");
		printer.print(")");
	}

	public void visit(EndObjectPointCut p, Void arg) {
		printer.print("endObject(");
		if(p.getTargetType() != null && p.getId() != null){
			p.getTargetType().accept(this, arg);
			printer.print(p.getId());
		}
		printer.print(")");
	}

	public void visit(NotPointCut p, Void arg) {
		printer.print("!");
		if (p.getPointCut() instanceof CombinedPointCut)
			printer.print("(");
		p.getPointCut().accept(this, arg);
		if (p.getPointCut() instanceof CombinedPointCut)
			printer.print(")");
	}

	public void visit(ConditionPointCut p, Void arg) {
		printer.print("condition(");
		p.getExpression().accept(this, arg);
		// printer.print("/* moved to the event definition */");
		printer.print(")");
	}

	public void visit(CountCondPointCut p, Void arg) {
		printer.print("countCond(");
		p.getExpression().accept(this, arg);
		// printer.print("/* moved to the event definition */");
		printer.print(")");
	}

	public void visit(IDPointCut p, Void arg) {
		printer.print(p.getId());
		printParameterTypes(p.getArgs(), arg);
	}

	public void visit(IFPointCut p, Void arg) {
		printer.print("if(");
		p.getExpression().accept(this, arg);
		printer.print(")");
	}

	public void visit(CFlowPointCut p, Void arg) {
		printer.print(p.getType() + "(");
		p.getPointCut().accept(this, arg);
		printer.print(")");
	}

	public void visit(FieldPointCut p, Void arg) {
		printer.print(p.getType() + "(");
		p.getField().accept(this, arg);
		printer.print(")");
	}

	public void visit(MethodPointCut p, Void arg) {
		printer.print(p.getType() + "(");
		p.getSignature().accept(this, arg);
		printer.print(")");
	}

	public void visit(TargetPointCut p, Void arg) {
		printer.print("target(");
		p.getTarget().accept(this, arg);
		printer.print(")");
	}

	public void visit(ThisPointCut p, Void arg) {
		printer.print("this(");
		p.getTarget().accept(this, arg);
		printer.print(")");
	}

	public void visit(WithinPointCut p, Void arg) {
		printer.print("within(");
		p.getPattern().accept(this, arg);
		printer.print(")");
	}

	public void visit(FieldPattern p, Void arg) {
		printModifiers(p.getModifiers());
		printNotModifiers(p.getNotModifiers());

		p.getType().accept(this, arg);
		printer.print(" ");

		if (p.getOwner() != null) {
			p.getOwner().accept(this, arg);
			printer.print(".");
		}
		printer.print(p.getMemberName());
	}

	public void visit(MethodPattern p, Void arg) {
		printModifiers(p.getModifiers());
		printNotModifiers(p.getNotModifiers());

		if (p.getMemberName().compareTo("new") != 0) {
			p.getType().accept(this, arg);
			printer.print(" ");
		}

		if (p.getOwner() != null) {
			p.getOwner().accept(this, arg);
			printer.print(".");
		}
		printer.print(p.getMemberName());
		printParameterTypes(p.getParameters(), arg);
		if (p.getThrows() != null) {
			printer.print("throws ");
			printTypePatterns(p.getThrows(), arg);
		}
	}

	public void visit(CombinedTypePattern p, Void arg) {
		for (Iterator<TypePattern> i = p.getSubTypes().iterator(); i.hasNext();) {
			TypePattern subP = i.next();
			if ((p.getSubTypes().size() > 1) && (subP instanceof CombinedTypePattern))
				printer.print("(");
			subP.accept(this, arg);
			if ((p.getSubTypes().size() > 1) && (subP instanceof CombinedTypePattern))
				printer.print(")");
			if (i.hasNext()) {
				printer.print(" " + p.getOp() + " ");
			}
		}
	}

	public void visit(NotTypePattern p, Void arg) {
		printer.print("!");
		if (p.getType() instanceof CombinedTypePattern)
			printer.print("(");
		p.getType().accept(this, arg);
		if (p.getType() instanceof CombinedTypePattern)
			printer.print(")");
	}

	public void visit(BaseTypePattern p, Void arg) {
		printer.print(p.getOp());
	}
}
