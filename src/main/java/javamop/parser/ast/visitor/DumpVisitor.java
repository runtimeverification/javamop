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

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.SourcePrinter;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.expr.QualifiedNameExpr;
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

public class DumpVisitor implements VoidVisitor<Object> {

	protected final SourcePrinter printer = new SourcePrinter();
	private CountCondPointCut p;
	private Object arg;

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

	protected void printMembers(List<BodyDeclaration> members, Object arg) {
		for (BodyDeclaration member : members) {
			printer.printLn();
			member.accept(this, arg);
			printer.printLn();
		}
	}

	protected void printMemberAnnotations(List<AnnotationExpr> annotations, Object arg) {
		if (annotations != null) {
			for (AnnotationExpr a : annotations) {
				a.accept(this, arg);
				printer.printLn();
			}
		}
	}

	protected void printAnnotations(List<AnnotationExpr> annotations, Object arg) {
		if (annotations != null) {
			for (AnnotationExpr a : annotations) {
				a.accept(this, arg);
				printer.print(" ");
			}
		}
	}

	protected void printTypeArgs(List<Type> args, Object arg) {
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

	protected void printTypeParameters(List<TypeParameter> args, Object arg) {
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

	protected void printSpecParameters(MOPParameters args, Object arg) {
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

	public void visit(Node n, Object arg) {
		throw new IllegalStateException(n.getClass().getName());
	}

	/* visit functions for JavaMOP components */

	public void visit(MOPSpecFile f, Object arg) {
		if (f.getPakage() != null)
			f.getPakage().accept(this, arg);
		if (f.getImports() != null) {
			for (ImportDeclaration i : f.getImports()) {
				i.accept(this, arg);
			}
			printer.printLn();
		}
		if (f.getSpecs() != null) {
			for (JavaMOPSpec i : f.getSpecs()) {
				i.accept(this, arg);
				printer.printLn();
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
		printer.printLn(" {");
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
		printer.printLn("}");
	}

	public void visit(MOPParameter p, Object arg) {
		p.getType().accept(this, arg);
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
			e.getAction().accept(this, arg);
		}
		printer.printLn();
	}

	public void visit(PropertyAndHandlers p, Object arg) {
		p.getProperty().accept(this, arg);
		printer.printLn();
		for (String event : p.getHandlers().keySet()) {
			BlockStmt stmt = p.getHandlers().get(event);
			printer.printLn("@" + event);
			printer.indent();
			stmt.accept(this, arg);
			printer.unindent();
			printer.printLn();
		}
	}

	public void visit(Formula f, Object arg) {
		printer.print(f.getType() + ": " + f.getFormula());
	}

	/* visit functions for AspectJ components */

	protected void printParameterTypes(List<TypePattern> args, Object arg) {
		printer.print("(");
		printTypePatterns(args, arg);
		printer.print(")");
	}

	protected void printTypePatterns(List<TypePattern> args, Object arg) {
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

	public void visit(WildcardParameter w, Object arg) {
		printer.print("..");
	}

	public void visit(ArgsPointCut p, Object arg) {
		printer.print("args");
		printParameterTypes(p.getArgs(), arg);
	}

	public void visit(CombinedPointCut p, Object arg) {
		for (Iterator<PointCut> i = p.getPointcuts().iterator(); i.hasNext();) {
			PointCut subP = i.next();
			if ((p.getPointcuts().size() > 1) && (subP instanceof CombinedPointCut))
				printer.print("(");
			subP.accept(this, arg);
			if ((p.getPointcuts().size() > 1) && (subP instanceof CombinedPointCut))
				printer.print(")");
			if (i.hasNext()) {
				printer.print(" " + p.getType() + " ");
			}
		}
	}

	public void visit(ThreadPointCut p, Object arg) {
		printer.print("thread(" + p.getId() + ")");
	}
	
	public void visit(ThreadNamePointCut p, Object arg) {
		printer.print("threadName(" + p.getId() + ")");
	}
	
	public void visit(ThreadBlockedPointCut p, Object arg) {
		printer.print("threadBlocked(" + p.getId() + ")");
	}

	public void visit(EndProgramPointCut p, Object arg) {
		printer.print("endProgram()");
	}

	public void visit(EndThreadPointCut p, Object arg) {
		printer.print("endThread(");
		printer.print(")");
	}

	public void visit(StartThreadPointCut p, Object arg) {
		printer.print("startThread(");
		printer.print(")");
	}

	public void visit(EndObjectPointCut p, Object arg) {
		printer.print("endObject(");
		if(p.getTargetType() != null && p.getId() != null){
			p.getTargetType().accept(this, arg);
			printer.print(p.getId());
		}
		printer.print(")");
	}
	
	public void visit(NotPointCut p, Object arg) {
		printer.print("!");
		if (p.getPointCut() instanceof CombinedPointCut)
			printer.print("(");
		p.getPointCut().accept(this, arg);
		if (p.getPointCut() instanceof CombinedPointCut)
			printer.print(")");
	}

	public void visit(ConditionPointCut p, Object arg) {
		printer.print("condition(");
		p.getExpression().accept(this, arg);
		// printer.print("/* moved to the event definition */");
		printer.print(")");
	}
	
	public void visit(CountCondPointCut p, Object arg) {
		this.p = p;
		this.arg = arg;
		printer.print("countCond(");
		p.getExpression().accept(this, arg);
		// printer.print("/* moved to the event definition */");
		printer.print(")");
	}

	public void visit(IDPointCut p, Object arg) {
		printer.print(p.getId());
		printParameterTypes(p.getArgs(), arg);
	}

	public void visit(IFPointCut p, Object arg) {
		printer.print("if(");
		p.getExpression().accept(this, arg);
		printer.print(")");
	}

	public void visit(CFlowPointCut p, Object arg) {
		printer.print(p.getType() + "(");
		p.getPointCut().accept(this, arg);
		printer.print(")");
	}

	public void visit(FieldPointCut p, Object arg) {
		printer.print(p.getType() + "(");
		p.getField().accept(this, arg);
		printer.print(")");
	}

	public void visit(MethodPointCut p, Object arg) {
		printer.print(p.getType() + "(");
		p.getSignature().accept(this, arg);
		printer.print(")");
	}

	public void visit(TargetPointCut p, Object arg) {
		printer.print("target(");
		p.getTarget().accept(this, arg);
		printer.print(")");
	}

	public void visit(ThisPointCut p, Object arg) {
		printer.print("this(");
		p.getTarget().accept(this, arg);
		printer.print(")");
	}

	public void visit(WithinPointCut p, Object arg) {
		printer.print("within(");
		p.getPattern().accept(this, arg);
		printer.print(")");
	}

	public void visit(FieldPattern p, Object arg) {
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

	public void visit(MethodPattern p, Object arg) {
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

	public void visit(CombinedTypePattern p, Object arg) {
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

	public void visit(NotTypePattern p, Object arg) {
		printer.print("!");
		if (p.getType() instanceof CombinedTypePattern)
			printer.print("(");
		p.getType().accept(this, arg);
		if (p.getType() instanceof CombinedTypePattern)
			printer.print(")");
	}

	public void visit(BaseTypePattern p, Object arg) {
		printer.print(p.getOp());
	}

	/** visit functions for Java components */

	public void visit(CompilationUnit n, Object arg) {
		if (n.getPakage() != null) {
			n.getPakage().accept(this, arg);
		}
		if (n.getImports() != null) {
			for (ImportDeclaration i : n.getImports()) {
				i.accept(this, arg);
			}
			printer.printLn();
		}
		if (n.getTypes() != null) {
			for (TypeDeclaration i : n.getTypes()) {
				i.accept(this, arg);
				printer.printLn();
			}
		}
	}

	public void visit(PackageDeclaration n, Object arg) {
		printAnnotations(n.getAnnotations(), arg);
		printer.print("package ");
		n.getName().accept(this, arg);
		printer.printLn(";");
		printer.printLn();
	}

	public void visit(NameExpr n, Object arg) {
		printer.print(n.getName());
	}

	@Override
	public void visit(Name n, Object arg) {

	}

	public void visit(QualifiedNameExpr n, Object arg) {
		n.getQualifier().accept(this, arg);
		printer.print(".");
		printer.print(n.getName());
	}

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

	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		if (n.isInterface()) {
			printer.print("interface ");
		} else {
			printer.print("class ");
		}

		printer.print(n.getName());

		printTypeParameters(n.getTypeParameters(), arg);

		if (n.getExtends() != null) {
			printer.print(" extends ");
			for (Iterator<ClassOrInterfaceType> i = n.getExtends().iterator(); i.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		if (n.getImplements() != null) {
			printer.print(" implements ");
			for (Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		printer.printLn(" {");
		printer.indent();
		if (n.getMembers() != null) {
			printMembers(n.getMembers(), arg);
		}
		printer.unindent();
		printer.print("}");
	}

	public void visit(ClassOrInterfaceType n, Object arg) {
		if (n.getScope() != null) {
			n.getScope().accept(this, arg);
			printer.print(".");
		}
		printer.print(n.getName());
		printTypeArgs(n.getTypeArgs(), arg);
	}

	public void visit(TypeParameter n, Object arg) {
		printer.print(n.getName());
		if (n.getTypeBound() != null) {
			printer.print(" extends ");
			for (Iterator<ClassOrInterfaceType> i = n.getTypeBound().iterator(); i.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(" & ");
				}
			}
		}
	}

	public void visit(PrimitiveType n, Object arg) {
		switch (n.getType()) {
		case Boolean:
			printer.print("boolean");
			break;
		case Byte:
			printer.print("byte");
			break;
		case Char:
			printer.print("char");
			break;
		case Double:
			printer.print("double");
			break;
		case Float:
			printer.print("float");
			break;
		case Int:
			printer.print("int");
			break;
		case Long:
			printer.print("long");
			break;
		case Short:
			printer.print("short");
			break;
		}
	}

	@Override
	public void visit(RecordDeclaration n, Object arg) {

	}

	@Override
	public void visit(CompactConstructorDeclaration n, Object arg) {

	}

	public void visit(ReferenceType n, Object arg) {
		n.getType().accept(this, arg);
		for (int i = 0; i < n.getArrayCount(); i++) {
			printer.print("[]");
		}
	}

	public void visit(WildcardType n, Object arg) {
		printer.print("?");
		if (n.getExtends() != null) {
			printer.print(" extends ");
			n.getExtends().accept(this, arg);
		}
		if (n.getSuper() != null) {
			printer.print(" super ");
			n.getSuper().accept(this, arg);
		}
	}

	@Override
	public void visit(ModuleDeclaration n, Object arg) {

	}

	@Override
	public void visit(ModuleRequiresDirective n, Object arg) {

	}

	@Override
	public void visit(ModuleExportsDirective n, Object arg) {

	}

	@Override
	public void visit(ModuleProvidesDirective n, Object arg) {

	}

	@Override
	public void visit(ModuleUsesDirective n, Object arg) {

	}

	@Override
	public void visit(ModuleOpensDirective n, Object arg) {

	}

	@Override
	public void visit(UnparsableStmt n, Object arg) {

	}

	@Override
	public void visit(ReceiverParameter n, Object arg) {

	}

	@Override
	public void visit(VarType n, Object arg) {

	}

	@Override
	public void visit(Modifier n, Object arg) {

	}

	@Override
	public void visit(SwitchExpr switchExpr, Object arg) {

	}

	@Override
	public void visit(TextBlockLiteralExpr n, Object arg) {

	}

	@Override
	public void visit(YieldStmt yieldStmt, Object arg) {

	}

	@Override
	public void visit(PatternExpr n, Object arg) {

	}

	public void visit(FieldDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());
		n.getType().accept(this, arg);

		printer.print(" ");
		for (Iterator<VariableDeclarator> i = n.getVariables().iterator(); i.hasNext();) {
			VariableDeclarator var = i.next();
			var.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}

		printer.print(";");
	}

	public void visit(VariableDeclarator n, Object arg) {
		n.getId().accept(this, arg);
		if (n.getInit() != null) {
			printer.print(" = ");
			n.getInit().accept(this, arg);
		}
	}

	public void visit(ArrayInitializerExpr n, Object arg) {
		printer.print("{");
		if (n.getValues() != null) {
			printer.print(" ");
			for (Iterator<Expression> i = n.getValues().iterator(); i.hasNext();) {
				Expression expr = i.next();
				expr.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(" ");
		}
		printer.print("}");
	}

	@Override
	public void visit(ArrayType n, Object arg) {

	}

	public void visit(VoidType n, Object arg) {
		printer.print("void");
	}

	public void visit(ArrayAccessExpr n, Object arg) {
		n.getName().accept(this, arg);
		printer.print("[");
		n.getIndex().accept(this, arg);
		printer.print("]");
	}

	public void visit(ArrayCreationExpr n, Object arg) {
		printer.print("new ");
		n.getType().accept(this, arg);
		printTypeArgs(n.getTypeArgs(), arg);

		if (n.getDimensions() != null) {
			for (Expression dim : n.getDimensions()) {
				printer.print("[");
				dim.accept(this, arg);
				printer.print("]");
			}
			for (int i = 0; i < n.getArrayCount(); i++) {
				printer.print("[]");
			}
		} else {
			for (int i = 0; i < n.getArrayCount(); i++) {
				printer.print("[]");
			}
			printer.print(" ");
			n.getInitializer().accept(this, arg);
		}
	}

	@Override
	public void visit(ArrayCreationLevel n, Object arg) {

	}

	public void visit(AssignExpr n, Object arg) {
		n.getTarget().accept(this, arg);
		printer.print(" ");
		switch (n.getOperator()) {
		case assign:
			printer.print("=");
			break;
		case and:
			printer.print("&=");
			break;
		case or:
			printer.print("|=");
			break;
		case xor:
			printer.print("^=");
			break;
		case plus:
			printer.print("+=");
			break;
		case minus:
			printer.print("-=");
			break;
		case rem:
			printer.print("%=");
			break;
		case slash:
			printer.print("/=");
			break;
		case star:
			printer.print("*=");
			break;
		case lShift:
			printer.print("<<=");
			break;
		case rSignedShift:
			printer.print(">>=");
			break;
		case rUnsignedShift:
			printer.print(">>>=");
			break;
		}
		printer.print(" ");
		n.getValue().accept(this, arg);
	}

	public void visit(BinaryExpr n, Object arg) {
		n.getLeft().accept(this, arg);
		printer.print(" ");
		switch (n.getOperator()) {
		case or:
			printer.print("||");
			break;
		case and:
			printer.print("&&");
			break;
		case binOr:
			printer.print("|");
			break;
		case binAnd:
			printer.print("&");
			break;
		case xor:
			printer.print("^");
			break;
		case equals:
			printer.print("==");
			break;
		case notEquals:
			printer.print("!=");
			break;
		case less:
			printer.print("<");
			break;
		case greater:
			printer.print(">");
			break;
		case lessEquals:
			printer.print("<=");
			break;
		case greaterEquals:
			printer.print(">=");
			break;
		case lShift:
			printer.print("<<");
			break;
		case rSignedShift:
			printer.print(">>");
			break;
		case rUnsignedShift:
			printer.print(">>>");
			break;
		case plus:
			printer.print("+");
			break;
		case minus:
			printer.print("-");
			break;
		case times:
			printer.print("*");
			break;
		case divide:
			printer.print("/");
			break;
		case remainder:
			printer.print("%");
			break;
		}
		printer.print(" ");
		n.getRight().accept(this, arg);
	}

	@Override
	public void visit(BlockComment n, Object arg) {

	}

	public void visit(CastExpr n, Object arg) {
		printer.print("(");
		n.getType().accept(this, arg);
		printer.print(") ");
		n.getExpr().accept(this, arg);
	}

	public void visit(ClassExpr n, Object arg) {
		n.getType().accept(this, arg);
		printer.print(".class");
	}

	public void visit(ConditionalExpr n, Object arg) {
		n.getCondition().accept(this, arg);
		printer.print(" ? ");
		n.getThenExpr().accept(this, arg);
		printer.print(" : ");
		n.getElseExpr().accept(this, arg);
	}

	public void visit(EnclosedExpr n, Object arg) {
		printer.print("(");
		n.getInner().accept(this, arg);
		printer.print(")");
	}

	public void visit(FieldAccessExpr n, Object arg) {
		n.getScope().accept(this, arg);
		printer.print(".");
		printer.print(n.getField());
	}

	public void visit(InstanceOfExpr n, Object arg) {
		n.getExpr().accept(this, arg);
		printer.print(" instanceof ");
		n.getType().accept(this, arg);
	}

	public void visit(CharLiteralExpr n, Object arg) {
		printer.print("'");
		printer.print(n.getValue());
		printer.print("'");
	}

	public void visit(DoubleLiteralExpr n, Object arg) {
		printer.print(n.getValue());
	}

	public void visit(IntegerLiteralExpr n, Object arg) {
		printer.print(n.getValue());
	}

	@Override
	public void visit(IntersectionType n, Object arg) {

	}

	@Override
	public void visit(JavadocComment n, Object arg) {

	}

	public void visit(LongLiteralExpr n, Object arg) {
		printer.print(n.getValue());
	}

	public void visit(StringLiteralExpr n, Object arg) {
		printer.print("\"");
		printer.print(n.getValue());
		printer.print("\"");
	}

	public void visit(BooleanLiteralExpr n, Object arg) {
		printer.print(n.getValue().toString());
	}

	public void visit(NullLiteralExpr n, Object arg) {
		printer.print("null");
	}

	public void visit(ThisExpr n, Object arg) {
		if (n.getClassExpr() != null) {
			n.getClassExpr().accept(this, arg);
			printer.print(".");
		}
		printer.print("this");
	}

	public void visit(SuperExpr n, Object arg) {
		if (n.getClassExpr() != null) {
			n.getClassExpr().accept(this, arg);
			printer.print(".");
		}
		printer.print("super");
	}

	public void visit(MethodCallExpr n, Object arg) {
		if (n.getScope() != null) {
			n.getScope().accept(this, arg);
			printer.print(".");
		}
		printTypeArgs(n.getTypeArgs(), arg);
		printer.print(n.getName());
		printer.print("(");
		if (n.getArgs() != null) {
			for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");
	}

	public void visit(ObjectCreationExpr n, Object arg) {
		if (n.getScope() != null) {
			n.getScope().accept(this, arg);
			printer.print(".");
		}

		printer.print("new ");

		printTypeArgs(n.getTypeArgs(), arg);
		n.getType().accept(this, arg);

		printer.print("(");
		if (n.getArgs() != null) {
			for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");

		if (n.getAnonymousClassBody() != null) {
			printer.printLn(" {");
			printer.indent();
			printMembers(n.getAnonymousClassBody(), arg);
			printer.unindent();
			printer.print("}");
		}
	}

	public void visit(UnaryExpr n, Object arg) {
		switch (n.getOperator()) {
		case positive:
			printer.print("+");
			break;
		case negative:
			printer.print("-");
			break;
		case inverse:
			printer.print("~");
			break;
		case not:
			printer.print("!");
			break;
		case preIncrement:
			printer.print("++");
			break;
		case preDecrement:
			printer.print("--");
			break;
		}

		n.getExpr().accept(this, arg);

		switch (n.getOperator()) {
		case posIncrement:
			printer.print("++");
			break;
		case posDecrement:
			printer.print("--");
			break;
		}
	}

	@Override
	public void visit(UnionType n, Object arg) {

	}

	@Override
	public void visit(UnknownType n, Object arg) {

	}

	public void visit(ConstructorDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printTypeParameters(n.getTypeParameters(), arg);
		if (n.getTypeParameters() != null) {
			printer.print(" ");
		}
		printer.print(n.getName());

		printer.print("(");
		if (n.getParameters() != null) {
			for (Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext();) {
				Parameter p = i.next();
				p.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");

		if (n.getThrows() != null) {
			printer.print(" throws ");
			for (Iterator<NameExpr> i = n.getThrows().iterator(); i.hasNext();) {
				NameExpr name = i.next();
				name.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(" ");
		n.getBlock().accept(this, arg);
	}

	public void visit(MethodDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printTypeParameters(n.getTypeParameters(), arg);
		if (n.getTypeParameters() != null) {
			printer.print(" ");
		}

		n.getType().accept(this, arg);
		printer.print(" ");
		printer.print(n.getName());

		printer.print("(");
		if (n.getParameters() != null) {
			for (Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext();) {
				Parameter p = i.next();
				p.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");

		for (int i = 0; i < n.getArrayCount(); i++) {
			printer.print("[]");
		}

		if (n.getThrows() != null) {
			printer.print(" throws ");
			for (Iterator<NameExpr> i = n.getThrows().iterator(); i.hasNext();) {
				NameExpr name = i.next();
				name.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		if (n.getBody() == null) {
			printer.print(";");
		} else {
			printer.print(" ");
			n.getBody().accept(this, arg);
		}
	}

	@Override
	public void visit(MethodReferenceExpr n, Object arg) {

	}

	public void visit(Parameter n, Object arg) {
		printAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		n.getType().accept(this, arg);
		if (n.isVarArgs()) {
			printer.print("...");
		}
		printer.print(" ");
		n.getId().accept(this, arg);
	}

	public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
		if (n.isThis()) {
			printTypeArgs(n.getTypeArgs(), arg);
			printer.print("this");
		} else {
			if (n.getExpr() != null) {
				n.getExpr().accept(this, arg);
				printer.print(".");
			}
			printTypeArgs(n.getTypeArgs(), arg);
			printer.print("super");
		}
		printer.print("(");
		if (n.getArgs() != null) {
			for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(");");
	}

	public void visit(VariableDeclarationExpr n, Object arg) {
		printAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		n.getType().accept(this, arg);
		printer.print(" ");

		for (Iterator<VariableDeclarator> i = n.getVars().iterator(); i.hasNext();) {
			VariableDeclarator v = i.next();
			v.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}
	}

	public void visit(AssertStmt n, Object arg) {
		printer.print("assert ");
		n.getCheck().accept(this, arg);
		if (n.getMessage() != null) {
			printer.print(" : ");
			n.getMessage().accept(this, arg);
		}
		printer.print(";");
	}

	public void visit(BlockStmt n, Object arg) {
		printer.print("{");
		if (n.getStmts() != null) {
			printer.printLn();
			printer.indent();
			for (Statement s : n.getStmts()) {
				s.accept(this, arg);
				printer.printLn();
			}
			printer.unindent();
		} else {
			printer.print("\n");
		}
		printer.print("}");

	}

	public void visit(LabeledStmt n, Object arg) {
		printer.print(n.getLabel());
		printer.print(": ");
		n.getStmt().accept(this, arg);
	}

	@Override
	public void visit(LambdaExpr n, Object arg) {

	}

	@Override
	public void visit(LineComment n, Object arg) {

	}

	@Override
	public void visit(LocalClassDeclarationStmt n, Object arg) {

	}

	@Override
	public void visit(LocalRecordDeclarationStmt n, Object arg) {

	}

	public void visit(EmptyStmt n, Object arg) {
		printer.print(";");
	}

	public void visit(ExpressionStmt n, Object arg) {
		n.getExpression().accept(this, arg);
		printer.print(";");
	}

	public void visit(SwitchStmt n, Object arg) {
		printer.print("switch(");
		n.getSelector().accept(this, arg);
		printer.printLn(") {");
		if (n.getEntries() != null) {
			printer.indent();
			for (SwitchEntry e : n.getEntries()) {
				e.accept(this, arg);
			}
			printer.unindent();
		}
		printer.print("}");

	}

	public void visit(SwitchEntry n, Object arg) {
		if (n.getLabel() != null) {
			printer.print("case ");
			n.getLabel().accept(this, arg);
			printer.print(":");
		} else {
			printer.print("default:");
		}
		printer.printLn();
		printer.indent();
		if (n.getStmts() != null) {
			for (Statement s : n.getStmts()) {
				s.accept(this, arg);
				printer.printLn();
			}
		}
		printer.unindent();
	}

	public void visit(BreakStmt n, Object arg) {
		printer.print("break");
		if (n.getId() != null) {
			printer.print(" ");
			printer.print(n.getId());
		}
		printer.print(";");
	}

	public void visit(ReturnStmt n, Object arg) {
		printer.print("return");
		if (n.getExpr() != null) {
			printer.print(" ");
			n.getExpr().accept(this, arg);
		}
		printer.print(";");
	}

	@Override
	public void visit(SimpleName n, Object arg) {

	}

	public void visit(EnumDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printer.print("enum ");
		printer.print(n.getName());

		if (n.getImplements() != null) {
			printer.print(" implements ");
			for (Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		printer.printLn(" {");
		printer.indent();
		if (n.getEntries() != null) {
			printer.printLn();
			for (Iterator<EnumConstantDeclaration> i = n.getEntries().iterator(); i.hasNext();) {
				EnumConstantDeclaration e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		if (n.getMembers() != null) {
			printer.printLn(";");
			printMembers(n.getMembers(), arg);
		} else {
			printer.printLn();
		}
		printer.unindent();
		printer.print("}");
	}

	public void visit(EnumConstantDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printer.print(n.getName());

		if (n.getArgs() != null) {
			printer.print("(");
			for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
			printer.print(")");
		}

		if (n.getClassBody() != null) {
			printer.printLn(" {");
			printer.indent();
			printMembers(n.getClassBody(), arg);
			printer.unindent();
			printer.printLn("}");
		}
	}

	public void visit(InitializerDeclaration n, Object arg) {
		printer.print("static ");
		n.getBlock().accept(this, arg);
	}

	public void visit(IfStmt n, Object arg) {
		printer.print("if (");
		n.getCondition().accept(this, arg);
		printer.print(") ");
		n.getThenStmt().accept(this, arg);
		if (n.getElseStmt() != null) {
			printer.print(" else ");
			n.getElseStmt().accept(this, arg);
		}
	}

	public void visit(WhileStmt n, Object arg) {
		printer.print("while (");
		n.getCondition().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	public void visit(ContinueStmt n, Object arg) {
		printer.print("continue");
		if (n.getId() != null) {
			printer.print(" ");
			printer.print(n.getId());
		}
		printer.print(";");
	}

	public void visit(DoStmt n, Object arg) {
		printer.print("do ");
		n.getBody().accept(this, arg);
		printer.print(" while (");
		n.getCondition().accept(this, arg);
		printer.print(");");
	}

	public void visit(ForEachStmt n, Object arg) {
		printer.print("for (");
		n.getVariable().accept(this, arg);
		printer.print(" : ");
		n.getIterable().accept(this, arg);
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	public void visit(ForStmt n, Object arg) {
		printer.print("for (");
		if (n.getInit() != null) {
			for (Iterator<Expression> i = n.getInit().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print("; ");
		if (n.getCompare() != null) {
			n.getCompare().accept(this, arg);
		}
		printer.print("; ");
		if (n.getUpdate() != null) {
			for (Iterator<Expression> i = n.getUpdate().iterator(); i.hasNext();) {
				Expression e = i.next();
				e.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(") ");
		n.getBody().accept(this, arg);
	}

	public void visit(ThrowStmt n, Object arg) {
		printer.print("throw ");
		n.getExpr().accept(this, arg);
		printer.print(";");
	}

	public void visit(SynchronizedStmt n, Object arg) {
		printer.print("synchronized (");
		n.getExpr().accept(this, arg);
		printer.print(") ");
		n.getBlock().accept(this, arg);
	}

	public void visit(TryStmt n, Object arg) {
		printer.print("try ");
		n.getTryBlock().accept(this, arg);
		if (n.getCatchs() != null) {
			for (CatchClause c : n.getCatchs()) {
				c.accept(this, arg);
			}
		}
		if (n.getFinallyBlock() != null) {
			printer.print(" finally ");
			n.getFinallyBlock().accept(this, arg);
		}
	}

	@Override
	public void visit(TypeExpr n, Object arg) {

	}

	@Override
	public void visit(TypeParameter n, Object arg) {

	}

	public void visit(CatchClause n, Object arg) {
		printer.print(" catch (");
		n.getExcept().accept(this, arg);
		printer.print(") ");
		n.getCatchBlock().accept(this, arg);

	}

	@Override
	public void visit(NodeList n, Object arg) {

	}

	public void visit(AnnotationDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		printer.print("@interface ");
		printer.print(n.getName());
		printer.printLn(" {");
		printer.indent();
		if (n.getMembers() != null) {
			printMembers(n.getMembers(), arg);
		}
		printer.unindent();
		printer.print("}");
	}

	public void visit(AnnotationMemberDeclaration n, Object arg) {
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		n.getType().accept(this, arg);
		printer.print(" ");
		printer.print(n.getName());
		printer.print("()");
		if (n.getDefaultValue() != null) {
			printer.print(" default ");
			n.getDefaultValue().accept(this, arg);
		}
		printer.print(";");
	}

	public void visit(MarkerAnnotationExpr n, Object arg) {
		printer.print("@");
		n.getName().accept(this, arg);
	}

	public void visit(SingleMemberAnnotationExpr n, Object arg) {
		printer.print("@");
		n.getName().accept(this, arg);
		printer.print("(");
		n.getMemberValue().accept(this, arg);
		printer.print(")");
	}

	public void visit(NormalAnnotationExpr n, Object arg) {
		printer.print("@");
		n.getName().accept(this, arg);
		printer.print("(");
		for (Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext();) {
			MemberValuePair m = i.next();
			m.accept(this, arg);
			if (i.hasNext()) {
				printer.print(", ");
			}
		}
		printer.print(")");
	}

	public void visit(MemberValuePair n, Object arg) {
		printer.print(n.getName());
		printer.print(" = ");
		n.getValue().accept(this, arg);
	}
}
