// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
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
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.expr.QualifiedNameExpr;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

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

	@Override
	public TypePattern visit(LineComment n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(BlockComment n, Object arg) {
		return null;
	}

	public TypePattern visit(ImportDeclaration n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleDeclaration n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleRequiresDirective n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleExportsDirective n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleProvidesDirective n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleUsesDirective n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ModuleOpensDirective n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(UnparsableStmt n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ReceiverParameter n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(VarType n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(Modifier n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(SwitchExpr n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(YieldStmt n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(TextBlockLiteralExpr n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(PatternExpr n, Object arg) {
		return null;
	}

	public TypePattern visit(TypeParameter n, Object arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public TypePattern visit(ClassOrInterfaceDeclaration n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(RecordDeclaration n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(CompactConstructorDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(EnumDeclaration n, Object arg) {
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

	public TypePattern visit(ConstructorDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(MethodDeclaration n, Object arg) {
		return null;
	}

	public TypePattern visit(Parameter n, Object arg) {
		return null;
	}

	public TypePattern visit(InitializerDeclaration n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(JavadocComment n, Object arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public TypePattern visit(ClassOrInterfaceType n, Object arg) {
		return null;
	}

	public TypePattern visit(PrimitiveType n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ArrayType n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(ArrayCreationLevel n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(IntersectionType n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(UnionType n, Object arg) {
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

	@Override
	public TypePattern visit(UnknownType n, Object arg) {
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

	@Override
	public TypePattern visit(LocalClassDeclarationStmt n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(LocalRecordDeclarationStmt n, Object arg) {
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

	@Override
	public TypePattern visit(SwitchEntry n, Object arg) {
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

	@Override
	public TypePattern visit(ForEachStmt n, Object arg) {
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

	@Override
	public TypePattern visit(LambdaExpr n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(MethodReferenceExpr n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(TypeExpr n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(NodeList n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(Name n, Object arg) {
		return null;
	}

	@Override
	public TypePattern visit(SimpleName n, Object arg) {
		return null;
	}
}
