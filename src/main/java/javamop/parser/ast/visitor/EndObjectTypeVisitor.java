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

public class EndObjectTypeVisitor implements GenericVisitor<TypePattern, Object> {

	public TypePattern visit(Node n, Object arg) {
		return null;
	}

	// - JavaMOP components

	public TypePattern visit(MOPSpecFile f, Object arg) {
		return null;
	}

	public TypePattern visit(JavaMOPSpec s, Object arg) {
		return null;
	}

	public TypePattern visit(MOPParameter p, Object arg) {
		return null;
	}

	public TypePattern visit(EventDefinition e, Object arg) {
		return null;
	}

	public TypePattern visit(PropertyAndHandlers p, Object arg) {
		return null;
	}

	public TypePattern visit(Formula f, Object arg) {
		return null;
	}

	// - AspectJ components --------------------

	public TypePattern visit(WildcardParameter w, Object arg) {
		return null;
	}

	public TypePattern visit(ArgsPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(CombinedPointCut p, Object arg) {
		TypePattern endObjectType = null;

		for (PointCut p2 : p.getPointcuts()) {
			TypePattern temp = p2.accept(this, arg);
			if (temp != null && endObjectType != null)
				return null;

			endObjectType = temp;
		}

		return endObjectType;
	}

	public TypePattern visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public TypePattern visit(ConditionPointCut p, Object arg) {
		return null;
	}
	
	public TypePattern visit(CountCondPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(FieldPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(MethodPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(TargetPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(ThisPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public TypePattern visit(IFPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(IDPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(WithinPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(ThreadPointCut p, Object arg) {
		return null;
	}
	
	public TypePattern visit(ThreadNamePointCut p, Object arg) {
		return null;
	}
	
	public TypePattern visit(ThreadBlockedPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(EndProgramPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(EndThreadPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(EndObjectPointCut p, Object arg) {
		return p.getTargetType();
	}

	public TypePattern visit(StartThreadPointCut p, Object arg) {
		return null;
	}

	public TypePattern visit(FieldPattern p, Object arg) {
		return null;
	}

	public TypePattern visit(MethodPattern p, Object arg) {
		return null;
	}

	public TypePattern visit(CombinedTypePattern p, Object arg) {
		return null;
	}

	public TypePattern visit(NotTypePattern p, Object arg) {
		return null;
	}

	public TypePattern visit(BaseTypePattern p, Object arg) {
		return null;
	}

	// - Compilation Unit ----------------------------------

	public TypePattern visit(CompilationUnit n, Object arg) {
		return null;
	}

	public TypePattern visit(PackageDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(ImportDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(TypeParameter n, Object arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public TypePattern visit(ClassOrInterfaceDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(EnumDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(EmptyTypeDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(EnumConstantDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(AnnotationDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(AnnotationMemberDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(FieldDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(VariableDeclarator n, Object arg) {
		return null;
	}

	public TypePattern visit(VariableDeclaratorId n, Object arg) {
		return null;
	}

	public TypePattern visit(ConstructorDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(MethodDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(Parameter n, Object arg) {
		return null;
	}

	public TypePattern visit(EmptyMemberDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(InitializerDeclaration n, Object arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public TypePattern visit(ClassOrInterfaceType n, Object arg) {
		return null;
	}

	public TypePattern visit(PrimitiveType n, Object arg) {
		return null;
	}

	public TypePattern visit(ReferenceType n, Object arg) {
		return null;
	}

	public TypePattern visit(VoidType n, Object arg) {
		return null;
	}

	public TypePattern visit(WildcardType n, Object arg) {
		return null;
	}

	// - Expression ----------------------------------------

	public TypePattern visit(ArrayAccessExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ArrayCreationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ArrayInitializerExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(AssignExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(BinaryExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(CastExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ClassExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ConditionalExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(EnclosedExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(FieldAccessExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(InstanceOfExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(StringLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(IntegerLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(LongLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(IntegerLiteralMinValueExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(LongLiteralMinValueExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(CharLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(DoubleLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(BooleanLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(NullLiteralExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(MethodCallExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(NameExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ObjectCreationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(QualifiedNameExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(SuperMemberAccessExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(ThisExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(SuperExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(UnaryExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(VariableDeclarationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(MarkerAnnotationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(SingleMemberAnnotationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(NormalAnnotationExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(MemberValuePair n, Object arg) {
		return null;
	}

	// - Statements ----------------------------------------

	public TypePattern visit(ExplicitConstructorInvocationStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(TypeDeclarationStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(AssertStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(BlockStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(LabeledStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(EmptyStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ExpressionStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(SwitchStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(SwitchEntryStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(BreakStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ReturnStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(IfStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(WhileStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ContinueStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(DoStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ForeachStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ForStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(ThrowStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(SynchronizedStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(TryStmt n, Object arg) {
		return null;
	}

	public TypePattern visit(CatchClause n, Object arg) {
		return null;
	}

}
