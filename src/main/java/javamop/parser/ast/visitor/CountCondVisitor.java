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
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.aspectj.*;



public class CountCondVisitor implements GenericVisitor<String, Object> {

	public String visit(Node n, Object arg) {
		return null;
	}

	// - JavaMOP components

	public String visit(MOPSpecFile f, Object arg) {
		return null;
	}

	public String visit(JavaMOPSpec s, Object arg) {
		return null;
	}

	public String visit(MOPParameter p, Object arg) {
		return null;
	}

	public String visit(EventDefinition e, Object arg) {
		return null;
	}

	public String visit(PropertyAndHandlers p, Object arg) {
		return null;
	}

	public String visit(Formula f, Object arg) {
		return null;
	}

	// - AspectJ components --------------------

	public String visit(WildcardParameter w, Object arg) {
		return null;
	}

	public String visit(ArgsPointCut p, Object arg) {
		return "";
	}

	public String visit(CombinedPointCut p, Object arg) {
		String condition = "";

		for (PointCut p2 : p.getPointcuts()) {
			String temp = p2.accept(this, arg);
			if (temp != null) {
				if (temp.length() != 0 && condition.length() != 0)
					return null;

				if (temp.length() != 0)
					condition = temp;
			} else
				return null;
		}

		return condition;
	}

	public String visit(NotPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public String visit(ConditionPointCut p, Object arg) {
		return "";
	}
	
	public String visit(CountCondPointCut p, Object arg) {
		String ret = p.getExpression().toString();
		if (ret == null || ret.length() == 0)
			return null;
		return ret;
	}
	
	public String visit(FieldPointCut p, Object arg) {
		return "";
	}

	public String visit(MethodPointCut p, Object arg) {
		return "";
	}

	public String visit(TargetPointCut p, Object arg) {
		return "";
	}

	public String visit(ThisPointCut p, Object arg) {
		return "";
	}

	public String visit(CFlowPointCut p, Object arg) {
		return p.getPointCut().accept(this, arg);
	}

	public String visit(IFPointCut p, Object arg) {
		return "";
	}

	public String visit(IDPointCut p, Object arg) {
		return "";
	}

	public String visit(WithinPointCut p, Object arg) {
		return "";
	}

	public String visit(ThreadPointCut p, Object arg) {
		return "";
	}
	
	public String visit(ThreadNamePointCut p, Object arg) {
		return "";
	}
	
	public String visit(ThreadBlockedPointCut p, Object arg) {
		return "";
	}

	public String visit(EndProgramPointCut p, Object arg) {
		return "";
	}

	public String visit(EndThreadPointCut p, Object arg) {
		return "";
	}
	
	public String visit(EndObjectPointCut p, Object arg) {
		return "";
	}

	public String visit(StartThreadPointCut p, Object arg) {
		return "";
	}

	public String visit(FieldPattern p, Object arg) {
		return null;
	}

	public String visit(MethodPattern p, Object arg) {
		return null;
	}

	public String visit(CombinedTypePattern p, Object arg) {
		return null;
	}

	public String visit(NotTypePattern p, Object arg) {
		return null;
	}

	public String visit(BaseTypePattern p, Object arg) {
		return null;
	}

	// - Compilation Unit ----------------------------------

	public String visit(CompilationUnit n, Object arg) {
		return null;
	}

	public String visit(PackageDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(TypeParameter n, Object arg) {
		return null;
	}

	@Override
	public String visit(LineComment n, Object arg) {
		return null;
	}

	@Override
	public String visit(BlockComment n, Object arg) {
		return null;
	}

	public String visit(ImportDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleRequiresDirective n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleExportsDirective n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleProvidesDirective n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleUsesDirective n, Object arg) {
		return null;
	}

	@Override
	public String visit(ModuleOpensDirective n, Object arg) {
		return null;
	}

	@Override
	public String visit(UnparsableStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(ReceiverParameter n, Object arg) {
		return null;
	}

	@Override
	public String visit(VarType n, Object arg) {
		return null;
	}

	@Override
	public String visit(Modifier n, Object arg) {
		return null;
	}

	@Override
	public String visit(SwitchExpr n, Object arg) {
		return null;
	}

	@Override
	public String visit(YieldStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(TextBlockLiteralExpr n, Object arg) {
		return null;
	}

	@Override
	public String visit(PatternExpr n, Object arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public String visit(ClassOrInterfaceDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(RecordDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(CompactConstructorDeclaration n, Object arg) {
		return null;
	}

	public String visit(EnumDeclaration n, Object arg) {
		return null;
	}

	public String visit(EnumConstantDeclaration n, Object arg) {
		return null;
	}

	public String visit(AnnotationDeclaration n, Object arg) {
		return null;
	}

	public String visit(AnnotationMemberDeclaration n, Object arg) {
		return null;
	}

	public String visit(FieldDeclaration n, Object arg) {
		return null;
	}

	public String visit(VariableDeclarator n, Object arg) {
		return null;
	}

	public String visit(ConstructorDeclaration n, Object arg) {
		return null;
	}

	public String visit(MethodDeclaration n, Object arg) {
		return null;
	}

	public String visit(Parameter n, Object arg) {
		return null;
	}

	public String visit(InitializerDeclaration n, Object arg) {
		return null;
	}

	@Override
	public String visit(JavadocComment n, Object arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public String visit(ClassOrInterfaceType n, Object arg) {
		return null;
	}

	public String visit(PrimitiveType n, Object arg) {
		return null;
	}

	@Override
	public String visit(ArrayType n, Object arg) {
		return null;
	}

	@Override
	public String visit(ArrayCreationLevel n, Object arg) {
		return null;
	}

	@Override
	public String visit(IntersectionType n, Object arg) {
		return null;
	}

	@Override
	public String visit(UnionType n, Object arg) {
		return null;
	}

	public String visit(ReferenceType n, Object arg) {
		return null;
	}

	public String visit(VoidType n, Object arg) {
		return null;
	}

	public String visit(WildcardType n, Object arg) {
		return null;
	}

	@Override
	public String visit(UnknownType n, Object arg) {
		return null;
	}

	// - Expression ----------------------------------------

	public String visit(ArrayAccessExpr n, Object arg) {
		return null;
	}

	public String visit(ArrayCreationExpr n, Object arg) {
		return null;
	}

	public String visit(ArrayInitializerExpr n, Object arg) {
		return null;
	}

	public String visit(AssignExpr n, Object arg) {
		return null;
	}

	public String visit(BinaryExpr n, Object arg) {
		return null;
	}

	public String visit(CastExpr n, Object arg) {
		return null;
	}

	public String visit(ClassExpr n, Object arg) {
		return null;
	}

	public String visit(ConditionalExpr n, Object arg) {
		return null;
	}

	public String visit(EnclosedExpr n, Object arg) {
		return null;
	}

	public String visit(FieldAccessExpr n, Object arg) {
		return null;
	}

	public String visit(InstanceOfExpr n, Object arg) {
		return null;
	}

	public String visit(StringLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(IntegerLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(LongLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(CharLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(DoubleLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(BooleanLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(NullLiteralExpr n, Object arg) {
		return null;
	}

	public String visit(MethodCallExpr n, Object arg) {
		return null;
	}

	public String visit(NameExpr n, Object arg) {
		return null;
	}

	public String visit(ObjectCreationExpr n, Object arg) {
		return null;
	}

	public String visit(QualifiedNameExpr n, Object arg) {
		return null;
	}

	public String visit(ThisExpr n, Object arg) {
		return null;
	}

	public String visit(SuperExpr n, Object arg) {
		return null;
	}

	public String visit(UnaryExpr n, Object arg) {
		return null;
	}

	public String visit(VariableDeclarationExpr n, Object arg) {
		return null;
	}

	public String visit(MarkerAnnotationExpr n, Object arg) {
		return null;
	}

	public String visit(SingleMemberAnnotationExpr n, Object arg) {
		return null;
	}

	public String visit(NormalAnnotationExpr n, Object arg) {
		return null;
	}

	public String visit(MemberValuePair n, Object arg) {
		return null;
	}

	// - Statements ----------------------------------------

	public String visit(ExplicitConstructorInvocationStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(LocalClassDeclarationStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(LocalRecordDeclarationStmt n, Object arg) {
		return null;
	}

	public String visit(AssertStmt n, Object arg) {
		return null;
	}

	public String visit(BlockStmt n, Object arg) {
		return null;
	}

	public String visit(LabeledStmt n, Object arg) {
		return null;
	}

	public String visit(EmptyStmt n, Object arg) {
		return null;
	}

	public String visit(ExpressionStmt n, Object arg) {
		return null;
	}

	public String visit(SwitchStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(SwitchEntry n, Object arg) {
		return null;
	}

	public String visit(BreakStmt n, Object arg) {
		return null;
	}

	public String visit(ReturnStmt n, Object arg) {
		return null;
	}

	public String visit(IfStmt n, Object arg) {
		return null;
	}

	public String visit(WhileStmt n, Object arg) {
		return null;
	}

	public String visit(ContinueStmt n, Object arg) {
		return null;
	}

	public String visit(DoStmt n, Object arg) {
		return null;
	}

	@Override
	public String visit(ForEachStmt n, Object arg) {
		return null;
	}

	public String visit(ForStmt n, Object arg) {
		return null;
	}

	public String visit(ThrowStmt n, Object arg) {
		return null;
	}

	public String visit(SynchronizedStmt n, Object arg) {
		return null;
	}

	public String visit(TryStmt n, Object arg) {
		return null;
	}

	public String visit(CatchClause n, Object arg) {
		return null;
	}

	@Override
	public String visit(LambdaExpr n, Object arg) {
		return null;
	}

	@Override
	public String visit(MethodReferenceExpr n, Object arg) {
		return null;
	}

	@Override
	public String visit(TypeExpr n, Object arg) {
		return null;
	}

	@Override
	public String visit(NodeList n, Object arg) {
		return null;
	}

	@Override
	public String visit(Name n, Object arg) {
		return null;
	}

	@Override
	public String visit(SimpleName n, Object arg) {
		return null;
	}
}
