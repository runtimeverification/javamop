// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;

import com.github.javaparser.ast.type.TypeParameter;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.aspectj.*;

import java.util.Collection;

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
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.GenericVisitor;

public class CheckThisJoinPointVisitor implements GenericVisitor<Boolean, Object> {

	public Boolean visit(Node n, Object arg) {
		return Boolean.FALSE;
	}

	// helper function
	private Boolean process(Object n, Object arg) {
		if (n == null)
			return Boolean.FALSE;

		if (n instanceof Node) {
			Node n2 = (Node) n;

			Boolean temp = n2.accept(this, arg);

			if (temp == true)
				return temp;
		} else if (n instanceof Collection) {
			Collection<?> c = (Collection<?>) n;

			for (Object o : c) {
				if (o instanceof Node) {
					Node n2 = (Node) o;

					Boolean temp = n2.accept(this, arg);

					if (temp == true)
						return temp;
				}
			}
		}

		return Boolean.FALSE;
	}

	// - JavaMOP components

	public Boolean visit(MOPSpecFile f, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(JavaMOPSpec s, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(MOPParameter p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(EventDefinition e, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(PropertyAndHandlers p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(Formula f, Object arg) {
		return Boolean.FALSE;
	}

	// - AspectJ components --------------------

	public Boolean visit(WildcardParameter w, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ArgsPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(CombinedPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(NotPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ConditionPointCut p, Object arg) {
		return Boolean.FALSE;
	}
	
	public Boolean visit(CountCondPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(FieldPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(MethodPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(TargetPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ThisPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(CFlowPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(IFPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(IDPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(WithinPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ThreadPointCut p, Object arg) {
		return Boolean.FALSE;
	}
	
	public Boolean visit(ThreadNamePointCut p, Object arg) {
		return Boolean.FALSE;
	}
	
	public Boolean visit(ThreadBlockedPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(EndProgramPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(EndThreadPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(EndObjectPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(StartThreadPointCut p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(FieldPattern p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(MethodPattern p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(CombinedTypePattern p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(NotTypePattern p, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(BaseTypePattern p, Object arg) {
		return Boolean.FALSE;
	}

	// - Compilation Unit ----------------------------------

	public Boolean visit(CompilationUnit n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(PackageDeclaration n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(LineComment n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(BlockComment n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ImportDeclaration n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleDeclaration n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleRequiresDirective n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleExportsDirective n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleProvidesDirective n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleUsesDirective n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ModuleOpensDirective n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(UnparsableStmt n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ReceiverParameter n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(VarType n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(Modifier n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(SwitchExpr n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(YieldStmt n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(TextBlockLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(PatternExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(TypeParameter n, Object arg) {
		return Boolean.FALSE;
	}

	// - Body ----------------------------------------------

	public Boolean visit(ClassOrInterfaceDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(RecordDeclaration n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(CompactConstructorDeclaration n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(EnumDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getEntries(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(EnumConstantDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getArguments(), arg) || process(n.getClassBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(AnnotationDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(AnnotationMemberDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getDefaultValue(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(FieldDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getVariables(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(VariableDeclarator n, Object arg) {
		if(process(n.getInitializer(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ConstructorDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getParameters(), arg) || process(n.getThrownExceptions(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(MethodDeclaration n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getParameters(), arg) || process(n.getThrownExceptions(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(Parameter n, Object arg) {
		if(process(n.getAnnotations(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(InitializerDeclaration n, Object arg) {
		if(process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(JavadocComment n, Object arg) {
		return Boolean.FALSE;
	}

	// - Type ----------------------------------------------

	public Boolean visit(ClassOrInterfaceType n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(PrimitiveType n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ArrayType n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ArrayCreationLevel n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(IntersectionType n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(UnionType n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ReferenceType n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(VoidType n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(WildcardType n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(UnknownType n, Object arg) {
		return Boolean.FALSE;
	}

	// - Expression ----------------------------------------

	public Boolean visit(ArrayAccessExpr n, Object arg) {
		if(process(n.getName(), arg) || process(n.getIndex(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ArrayCreationExpr n, Object arg) {
		//TODO: is getLevels a good replacement for getDimensions?
		if(process(n.getInitializer(), arg) || process(n.getLevels(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ArrayInitializerExpr n, Object arg) {
		if(process(n.getValues(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(AssignExpr n, Object arg) {
		if(process(n.getTarget(), arg) || process(n.getValue(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(BinaryExpr n, Object arg) {
		if(process(n.getLeft(), arg) || process(n.getRight(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(CastExpr n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ClassExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ConditionalExpr n, Object arg) {
		if(process(n.getCondition(), arg) || process(n.getThenExpr(), arg) || process(n.getElseExpr(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(EnclosedExpr n, Object arg) {
		if(process(n.getInner(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(FieldAccessExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(InstanceOfExpr n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(StringLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(IntegerLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(LongLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(CharLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(DoubleLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(BooleanLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(NullLiteralExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(MethodCallExpr n, Object arg) {
		if(process(n.getScope(), arg) || process(n.getArguments(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(NameExpr n, Object arg) {
		if(n.getName().equals("thisJoinPoint"))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ObjectCreationExpr n, Object arg) {
		if(process(n.getArguments(), arg) || process(n.getAnonymousClassBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ThisExpr n, Object arg) {
		if(process(n.asThisExpr(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(SuperExpr n, Object arg) {
		if(process(n.asSuperExpr(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(UnaryExpr n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(VariableDeclarationExpr n, Object arg) {
		if(process(n.getAnnotations(), arg) || process(n.getVariables(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(MarkerAnnotationExpr n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(SingleMemberAnnotationExpr n, Object arg) {
		if(process(n.getMemberValue(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(NormalAnnotationExpr n, Object arg) {
		if(process(n.getPairs(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(MemberValuePair n, Object arg) {
		if(process(n.getValue(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	// - Statements ----------------------------------------

	public Boolean visit(ExplicitConstructorInvocationStmt n, Object arg) {
		if(process(n.getExpression(), arg) || process(n.getArguments(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(LocalClassDeclarationStmt n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(LocalRecordDeclarationStmt n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(AssertStmt n, Object arg) {
		if(process(n.getCheck(), arg) || process(n.getMessage(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(BlockStmt n, Object arg) {
		if(process(n.getStatements(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(LabeledStmt n, Object arg) {
		if(process(n.getStatement(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(EmptyStmt n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ExpressionStmt n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(SwitchStmt n, Object arg) {
		if(process(n.getSelector(), arg) || process(n.getEntries(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(SwitchEntry n, Object arg) {
		if(process(n.getLabels(), arg) || process(n.getStatements(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(BreakStmt n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(ReturnStmt n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(IfStmt n, Object arg) {
		if(process(n.getCondition(), arg) || process(n.getThenStmt(), arg) || process(n.getElseStmt(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(WhileStmt n, Object arg) {
		if(process(n.getCondition(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ContinueStmt n, Object arg) {
		return Boolean.FALSE;
	}

	public Boolean visit(DoStmt n, Object arg) {
		if(process(n.getBody(), arg) || process(n.getCondition(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(ForEachStmt n, Object arg) {
		if(process(n.getVariable(), arg) || process(n.getIterable(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ForStmt n, Object arg) {
		if(process(n.getInitialization(), arg) || process(n.getCompare(), arg) || process(n.getUpdate(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(ThrowStmt n, Object arg) {
		if(process(n.getExpression(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(SynchronizedStmt n, Object arg) {
		if(process(n.getExpression(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(TryStmt n, Object arg) {
		if(process(n.getTryBlock(), arg) || process(n.getCatchClauses(), arg) || process(n.getFinallyBlock(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	public Boolean visit(CatchClause n, Object arg) {
		if(process(n.getParameter(), arg) || process(n.getBody(), arg))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(LambdaExpr n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(MethodReferenceExpr n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(TypeExpr n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(NodeList n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(Name n, Object arg) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visit(SimpleName n, Object arg) {
		return Boolean.FALSE;
	}
}
