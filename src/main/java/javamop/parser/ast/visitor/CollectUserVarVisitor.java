// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import java.util.ArrayList;
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

	@Override
	public List<String> visit(ModuleDeclaration n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ModuleRequiresDirective n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ModuleExportsDirective n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ModuleProvidesDirective n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ModuleUsesDirective n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ModuleOpensDirective n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(UnparsableStmt n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ReceiverParameter n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(VarType n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(Modifier n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(SwitchExpr n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(YieldStmt n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(TextBlockLiteralExpr n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(PatternExpr n, Object arg) {
		return null;
	}

	public List<String> visit(TypeParameter n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(LineComment n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(BlockComment n, Object arg) {
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

	@Override
	public List<String> visit(RecordDeclaration n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(CompactConstructorDeclaration n, Object arg) {
		return null;
	}

	public List<String> visit(EnumDeclaration n, Object arg) {
		List<String> ret = new ArrayList<String>();

		ret.add(n.getName());

		return ret;
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

	public List<String> visit(InitializerDeclaration n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(JavadocComment n, Object arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public List<String> visit(ClassOrInterfaceType n, Object arg) {
		return null;
	}

	public List<String> visit(PrimitiveType n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ArrayType n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(ArrayCreationLevel n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(IntersectionType n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(UnionType n, Object arg) {
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

	@Override
	public List<String> visit(UnknownType n, Object arg) {
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

	@Override
	public List<String> visit(LocalClassDeclarationStmt n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(LocalRecordDeclarationStmt n, Object arg) {
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

	@Override
	public List<String> visit(SwitchEntry n, Object arg) {
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

	@Override
	public List<String> visit(ForEachStmt n, Object arg) {
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

	@Override
	public List<String> visit(LambdaExpr n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(MethodReferenceExpr n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(TypeExpr n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(NodeList n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(Name n, Object arg) {
		return null;
	}

	@Override
	public List<String> visit(SimpleName n, Object arg) {
		return null;
	}

}
