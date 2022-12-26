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
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class BaseVisitor<R, A> implements GenericVisitor<R, A> {

	public R visit(Node n, A arg) {
		return null;
	}

	// - JavaMOP components

	public R visit(MOPSpecFile f, A arg) {
		return null;
	}

	public R visit(JavaMOPSpec s, A arg) {
		return null;
	}

	public R visit(MOPParameter p, A arg) {
		return null;
	}

	public R visit(EventDefinition e, A arg) {
		return null;
	}

	public R visit(PropertyAndHandlers p, A arg) {
		return null;
	}

	public R visit(Formula f, A arg) {
		return null;
	}

	// - AspectJ components --------------------

	public R visit(WildcardParameter w, A arg) {
		return null;
	}

	public R visit(ArgsPointCut p, A arg) {
		return null;
	}

	public R visit(CombinedPointCut p, A arg){
		return null;
	}

	public R visit(NotPointCut p, A arg) {
		return null;
	}

	public R visit(ConditionPointCut p, A arg) {
		return null;
	}
	
	public R visit(CountCondPointCut p, A arg) {
		return null;
	}
	
	public R visit(FieldPointCut p, A arg) {
		return null;
	}

	public R visit(MethodPointCut p, A arg) {
		return null;
	}

	public R visit(TargetPointCut p, A arg) {
		return null;
	}

	public R visit(ThisPointCut p, A arg) {
		return null;
	}

	public R visit(CFlowPointCut p, A arg) {
		return null;
	}

	public R visit(IFPointCut p, A arg) {
		return null;
	}

	public R visit(IDPointCut p, A arg) {
		return null;
	}

	public R visit(WithinPointCut p, A arg) {
		return null;
	}

	public R visit(ThreadPointCut p, A arg) {
		return null;
	}
	
	public R visit(ThreadNamePointCut p, A arg) {
		return null;
	}
	
	public R visit(ThreadBlockedPointCut p, A arg) {
		return null;
	}

	public R visit(EndProgramPointCut p, A arg) {
		return null;
	}

	public R visit(EndThreadPointCut p, A arg) {
		return null;
	}
	
	public R visit(EndObjectPointCut p, A arg) {
		return null;
	}

	public R visit(StartThreadPointCut p, A arg) {
		return null;
	}

	public R visit(FieldPattern p, A arg) {
		return null;
	}

	public R visit(MethodPattern p, A arg) {
		return null;
	}

	public R visit(CombinedTypePattern p, A arg) {
		return null;
	}

	public R visit(NotTypePattern p, A arg) {
		return null;
	}

	public R visit(BaseTypePattern p, A arg) {
		return null;
	}

	// - Compilation Unit ----------------------------------

	public R visit(CompilationUnit n, A arg) {
		return null;
	}

	public R visit(PackageDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(TypeParameter n, A arg) {
		return null;
	}

	@Override
	public R visit(LineComment n, A arg) {
		return null;
	}

	@Override
	public R visit(BlockComment n, A arg) {
		return null;
	}

	public R visit(ImportDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleRequiresDirective n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleExportsDirective n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleProvidesDirective n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleUsesDirective n, A arg) {
		return null;
	}

	@Override
	public R visit(ModuleOpensDirective n, A arg) {
		return null;
	}

	@Override
	public R visit(UnparsableStmt n, A arg) {
		return null;
	}

	@Override
	public R visit(ReceiverParameter n, A arg) {
		return null;
	}

	@Override
	public R visit(VarType n, A arg) {
		return null;
	}

	@Override
	public R visit(Modifier n, A arg) {
		return null;
	}

	@Override
	public R visit(SwitchExpr n, A arg) {
		return null;
	}

	@Override
	public R visit(YieldStmt n, A arg) {
		return null;
	}

	@Override
	public R visit(TextBlockLiteralExpr n, A arg) {
		return null;
	}

	@Override
	public R visit(PatternExpr n, A arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public R visit(ClassOrInterfaceDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(RecordDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(CompactConstructorDeclaration n, A arg) {
		return null;
	}

	public R visit(EnumDeclaration n, A arg) {
		return null;
	}

	public R visit(EnumConstantDeclaration n, A arg) {
		return null;
	}

	public R visit(AnnotationDeclaration n, A arg) {
		return null;
	}

	public R visit(AnnotationMemberDeclaration n, A arg) {
		return null;
	}

	public R visit(FieldDeclaration n, A arg) {
		return null;
	}

	public R visit(VariableDeclarator n, A arg) {
		return null;
	}

	public R visit(ConstructorDeclaration n, A arg) {
		return null;
	}

	public R visit(MethodDeclaration n, A arg) {
		return null;
	}

	public R visit(Parameter n, A arg) {
		return null;
	}

	public R visit(InitializerDeclaration n, A arg) {
		return null;
	}

	@Override
	public R visit(JavadocComment n, A arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public R visit(ClassOrInterfaceType n, A arg) {
		return null;
	}

	public R visit(PrimitiveType n, A arg) {
		return null;
	}

	@Override
	public R visit(ArrayType n, A arg) {
		return null;
	}

	@Override
	public R visit(ArrayCreationLevel n, A arg) {
		return null;
	}

	@Override
	public R visit(IntersectionType n, A arg) {
		return null;
	}

	@Override
	public R visit(UnionType n, A arg) {
		return null;
	}

	public R visit(ReferenceType n, A arg) {
		return null;
	}

	public R visit(VoidType n, A arg) {
		return null;
	}

	public R visit(WildcardType n, A arg) {
		return null;
	}

	@Override
	public R visit(UnknownType n, A arg) {
		return null;
	}

	// - Expression ----------------------------------------

	public R visit(ArrayAccessExpr n, A arg) {
		return null;
	}

	public R visit(ArrayCreationExpr n, A arg) {
		return null;
	}

	public R visit(ArrayInitializerExpr n, A arg) {
		return null;
	}

	public R visit(AssignExpr n, A arg) {
		return null;
	}

	public R visit(BinaryExpr n, A arg) {
		return null;
	}

	public R visit(CastExpr n, A arg) {
		return null;
	}

	public R visit(ClassExpr n, A arg) {
		return null;
	}

	public R visit(ConditionalExpr n, A arg) {
		return null;
	}

	public R visit(EnclosedExpr n, A arg) {
		return null;
	}

	public R visit(FieldAccessExpr n, A arg) {
		return null;
	}

	public R visit(InstanceOfExpr n, A arg) {
		return null;
	}

	public R visit(StringLiteralExpr n, A arg) {
		return null;
	}

	public R visit(IntegerLiteralExpr n, A arg) {
		return null;
	}

	public R visit(LongLiteralExpr n, A arg) {
		return null;
	}

	public R visit(CharLiteralExpr n, A arg) {
		return null;
	}

	public R visit(DoubleLiteralExpr n, A arg) {
		return null;
	}

	public R visit(BooleanLiteralExpr n, A arg) {
		return null;
	}

	public R visit(NullLiteralExpr n, A arg) {
		return null;
	}

	public R visit(MethodCallExpr n, A arg) {
		return null;
	}

	public R visit(NameExpr n, A arg) {
		return null;
	}

	public R visit(ObjectCreationExpr n, A arg) {
		return null;
	}

	public R visit(ThisExpr n, A arg) {
		return null;
	}

	public R visit(SuperExpr n, A arg) {
		return null;
	}

	public R visit(UnaryExpr n, A arg) {
		return null;
	}

	public R visit(VariableDeclarationExpr n, A arg) {
		return null;
	}

	public R visit(MarkerAnnotationExpr n, A arg) {
		return null;
	}

	public R visit(SingleMemberAnnotationExpr n, A arg) {
		return null;
	}

	public R visit(NormalAnnotationExpr n, A arg) {
		return null;
	}

	public R visit(MemberValuePair n, A arg) {
		return null;
	}

	// - Statements ----------------------------------------

	public R visit(ExplicitConstructorInvocationStmt n, A arg) {
		return null;
	}

	@Override
	public R visit(LocalClassDeclarationStmt n, A arg) {
		return null;
	}

	@Override
	public R visit(LocalRecordDeclarationStmt n, A arg) {
		return null;
	}

	public R visit(AssertStmt n, A arg) {
		return null;
	}

	public R visit(BlockStmt n, A arg) {
		return null;
	}

	public R visit(LabeledStmt n, A arg) {
		return null;
	}

	public R visit(EmptyStmt n, A arg) {
		return null;
	}

	public R visit(ExpressionStmt n, A arg) {
		return null;
	}

	public R visit(SwitchStmt n, A arg) {
		return null;
	}

	public R visit(SwitchEntry n, A arg) {
		return null;
	}

	public R visit(BreakStmt n, A arg) {
		return null;
	}

	public R visit(ReturnStmt n, A arg) {
		return null;
	}

	public R visit(IfStmt n, A arg) {
		return null;
	}

	public R visit(WhileStmt n, A arg) {
		return null;
	}

	public R visit(ContinueStmt n, A arg) {
		return null;
	}

	public R visit(DoStmt n, A arg) {
		return null;
	}

	public R visit(ForEachStmt n, A arg) {
		return null;
	}

	public R visit(ForStmt n, A arg) {
		return null;
	}

	public R visit(ThrowStmt n, A arg) {
		return null;
	}

	public R visit(SynchronizedStmt n, A arg) {
		return null;
	}

	public R visit(TryStmt n, A arg) {
		return null;
	}

	public R visit(CatchClause n, A arg) {
		return null;
	}

	@Override
	public R visit(LambdaExpr n, A arg) {
		return null;
	}

	@Override
	public R visit(MethodReferenceExpr n, A arg) {
		return null;
	}

	@Override
	public R visit(TypeExpr n, A arg) {
		return null;
	}

	@Override
	public R visit(NodeList n, A arg) {
		return null;
	}

	@Override
	public R visit(Name n, A arg) {
		return null;
	}

	@Override
	public R visit(SimpleName n, A arg) {
		return null;
	}

}
