// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import javamop.parser.ast.CompilationUnit;
import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.Node;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.TypeParameter;
import javamop.parser.ast.body.AnnotationDeclaration;
import javamop.parser.ast.body.AnnotationMemberDeclaration;
import javamop.parser.ast.body.ClassOrInterfaceDeclaration;
import javamop.parser.ast.body.ConstructorDeclaration;
import javamop.parser.ast.body.EmptyMemberDeclaration;
import javamop.parser.ast.body.EmptyTypeDeclaration;
import javamop.parser.ast.body.EnumConstantDeclaration;
import javamop.parser.ast.body.EnumDeclaration;
import javamop.parser.ast.body.FieldDeclaration;
import javamop.parser.ast.body.InitializerDeclaration;
import javamop.parser.ast.body.MethodDeclaration;
import javamop.parser.ast.body.Parameter;
import javamop.parser.ast.body.VariableDeclarator;
import javamop.parser.ast.body.VariableDeclaratorId;
import javamop.parser.ast.expr.*;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.*;
import javamop.parser.ast.type.*;
import javamop.parser.ast.aspectj.*;

import java.util.ArrayList;
import java.util.List;


public class CollectUserVarVisitor implements GenericVisitor<List<String>, Object> {

	public List<String> visit(Node n, Object arg) {
		return null;
	}

	// - JavaMOP components

	public List<String> visit(MOPSpecFile f, Object arg) {
		return null;
	}

	public List<String> visit(JavaMOPSpec s, Object arg) {
		return null;
	}

	public List<String> visit(MOPParameter p, Object arg) {
		return null;
	}

	public List<String> visit(EventDefinition e, Object arg) {
		return null;
	}

	public List<String> visit(PropertyAndHandlers p, Object arg) {
		return null;
	}

	public List<String> visit(Formula f, Object arg) {
		return null;
	}

	// - AspectJ components --------------------

	public List<String> visit(WildcardParameter w, Object arg) {
		return null;
	}

	public List<String> visit(ArgsPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(CombinedPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(NotPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(ConditionPointCut p, Object arg) {
		return null;
	}
	
	public List<String> visit(CountCondPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(FieldPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(MethodPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(TargetPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(ThisPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(CFlowPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(IFPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(IDPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(WithinPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(ThreadPointCut p, Object arg) {
		return null;
	}
	
	public List<String> visit(ThreadNamePointCut p, Object arg) {
		return null;
	}
	
	public List<String> visit(ThreadBlockedPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(EndProgramPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(EndThreadPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(EndObjectPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(StartThreadPointCut p, Object arg) {
		return null;
	}

	public List<String> visit(FieldPattern p, Object arg) {
		return null;
	}

	public List<String> visit(MethodPattern p, Object arg) {
		return null;
	}

	public List<String> visit(CombinedTypePattern p, Object arg) {
		return null;
	}

	public List<String> visit(NotTypePattern p, Object arg) {
		return null;
	}

	public List<String> visit(BaseTypePattern p, Object arg) {
		return null;
	}

	// - Compilation Unit ----------------------------------

	public List<String> visit(CompilationUnit n, Object arg) {
		return null;
	}

	public List<String> visit(PackageDeclaration n, Object arg) {
		return null;
	}

	public List<String> visit(ImportDeclaration n, Object arg) {
		return null;
	}

	public List<String> visit(TypeParameter n, Object arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public List<String> visit(ClassOrInterfaceDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null) {
					ret.addAll(temp);
				}
			}
		}

		return ret;
	}

	public List<String> visit(EnumDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(EmptyTypeDeclaration n, Object arg) {
		return null;
	}

	public List<String> visit(EnumConstantDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(AnnotationDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(AnnotationMemberDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		ret.add(n.getName());

		return ret;
	}

	public List<String> visit(FieldDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getAnnotations() != null) {
			for (AnnotationExpr a : n.getAnnotations()) {
				List<String> temp = a.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		if (n.getVariables() != null) {
			for (VariableDeclarator var : n.getVariables()) {
				List<String> temp = var.accept(this, arg);

				if (temp != null)
					ret.addAll(temp);
			}
		}

		return ret;
	}

	public List<String> visit(VariableDeclarator n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getId() != null) {
			List<String> temp = n.getId().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(VariableDeclaratorId n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());
		
		return ret;
	}

	public List<String> visit(ConstructorDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());
		
		return ret;
	}

	public List<String> visit(MethodDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());
		
		return ret;
	}

	public List<String> visit(Parameter n, Object arg) {
		return null;
	}

	public List<String> visit(EmptyMemberDeclaration n, Object arg) {
		return null;
	}

	public List<String> visit(InitializerDeclaration n, Object arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public List<String> visit(ClassOrInterfaceType n, Object arg) {
		return null;
	}

	public List<String> visit(PrimitiveType n, Object arg) {
		return null;
	}

	public List<String> visit(ReferenceType n, Object arg) {
		return null;
	}

	public List<String> visit(VoidType n, Object arg) {
		return null;
	}

	public List<String> visit(WildcardType n, Object arg) {
		return null;
	}

	// - Expression ----------------------------------------

	public List<String> visit(ArrayAccessExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ArrayCreationExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ArrayInitializerExpr n, Object arg) {
		return null;
	}

	public List<String> visit(AssignExpr n, Object arg) {
		return null;
	}

	public List<String> visit(BinaryExpr n, Object arg) {
		return null;
	}

	public List<String> visit(CastExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ClassExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ConditionalExpr n, Object arg) {
		return null;
	}

	public List<String> visit(EnclosedExpr n, Object arg) {
		return null;
	}

	public List<String> visit(FieldAccessExpr n, Object arg) {
		return null;
	}

	public List<String> visit(InstanceOfExpr n, Object arg) {
		return null;
	}

	public List<String> visit(StringLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(IntegerLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(LongLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(IntegerLiteralMinValueExpr n, Object arg) {
		return null;
	}

	public List<String> visit(LongLiteralMinValueExpr n, Object arg) {
		return null;
	}

	public List<String> visit(CharLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(DoubleLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(BooleanLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(NullLiteralExpr n, Object arg) {
		return null;
	}

	public List<String> visit(MethodCallExpr n, Object arg) {
		return null;
	}

	public List<String> visit(NameExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ObjectCreationExpr n, Object arg) {
		return null;
	}

	public List<String> visit(QualifiedNameExpr n, Object arg) {
		return null;
	}

	public List<String> visit(SuperMemberAccessExpr n, Object arg) {
		return null;
	}

	public List<String> visit(ThisExpr n, Object arg) {
		return null;
	}

	public List<String> visit(SuperExpr n, Object arg) {
		return null;
	}

	public List<String> visit(UnaryExpr n, Object arg) {
		return null;
	}

	public List<String> visit(VariableDeclarationExpr n, Object arg) {
		return null;
	}

	public List<String> visit(MarkerAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(SingleMemberAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(NormalAnnotationExpr n, Object arg) {
		List<String> ret = new ArrayList<String>();

		if (n.getName() != null) {
			List<String> temp = n.getName().accept(this, arg);
			if (temp != null)
				ret.addAll(temp);
		}

		return ret;
	}

	public List<String> visit(MemberValuePair n, Object arg) {
		return null;
	}

	// - Statements ----------------------------------------

	public List<String> visit(ExplicitConstructorInvocationStmt n, Object arg) {
		return null;
	}

	public List<String> visit(TypeDeclarationStmt n, Object arg) {
		return null;
	}

	public List<String> visit(AssertStmt n, Object arg) {
		return null;
	}

	public List<String> visit(BlockStmt n, Object arg) {
		return null;
	}

	public List<String> visit(LabeledStmt n, Object arg) {
		return null;
	}

	public List<String> visit(EmptyStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ExpressionStmt n, Object arg) {
		return null;
	}

	public List<String> visit(SwitchStmt n, Object arg) {
		return null;
	}

	public List<String> visit(SwitchEntryStmt n, Object arg) {
		return null;
	}

	public List<String> visit(BreakStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ReturnStmt n, Object arg) {
		return null;
	}

	public List<String> visit(IfStmt n, Object arg) {
		return null;
	}

	public List<String> visit(WhileStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ContinueStmt n, Object arg) {
		return null;
	}

	public List<String> visit(DoStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ForeachStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ForStmt n, Object arg) {
		return null;
	}

	public List<String> visit(ThrowStmt n, Object arg) {
		return null;
	}

	public List<String> visit(SynchronizedStmt n, Object arg) {
		return null;
	}

	public List<String> visit(TryStmt n, Object arg) {
		return null;
	}

	public List<String> visit(CatchClause n, Object arg) {
		return null;
	}

}
