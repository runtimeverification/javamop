// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;


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
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.mopspec.PropertyAndHandlers;

public class CollectMOPVarVisitor implements GenericVisitor<MOPParameters, MOPParameters> {

	public MOPParameters visit(Node n, MOPParameters arg) {
		return null;
	}

	// helper function
	private MOPParameters process(MOPParameters ret, Object n, MOPParameters arg) {
		if (n == null)
			return ret;

		if (n instanceof Node) {
			Node n2 = (Node) n;

			MOPParameters temp = n2.accept(this, arg);

			ret.addAll(temp);
		} else if (n instanceof Collection) {
			Collection<?> c = (Collection<?>) n;

			for (Object o : c) {
				if (o instanceof Node) {
					Node n2 = (Node) o;

					MOPParameters temp = n2.accept(this, arg);

					if (temp != null)
						ret.addAll(temp);
				}
			}
		}

		return ret;
	}

	// - JavaMOP components

	public MOPParameters visit(MOPSpecFile f, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(JavaMOPSpec s, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(MOPParameter p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(EventDefinition e, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(PropertyAndHandlers p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(Formula f, MOPParameters arg) {
		return null;
	}

	// - AspectJ components --------------------

	public MOPParameters visit(WildcardParameter w, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ArgsPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(CombinedPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(NotPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ConditionPointCut p, MOPParameters arg) {
		return null;
	}
	
	public MOPParameters visit(CountCondPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(FieldPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(MethodPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(TargetPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ThisPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(CFlowPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(IFPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(IDPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(WithinPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ThreadPointCut p, MOPParameters arg) {
		return null;
	}
	
	public MOPParameters visit(ThreadNamePointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ThreadBlockedPointCut p, MOPParameters arg) {
		return null;
	}
	
	public MOPParameters visit(EndProgramPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(EndThreadPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(EndObjectPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(StartThreadPointCut p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(FieldPattern p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(MethodPattern p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(CombinedTypePattern p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(NotTypePattern p, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(BaseTypePattern p, MOPParameters arg) {
		return null;
	}

	// - Compilation Unit ----------------------------------

	public MOPParameters visit(CompilationUnit n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(PackageDeclaration n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(LineComment n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(BlockComment n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ImportDeclaration n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleDeclaration n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleRequiresDirective n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleExportsDirective n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleProvidesDirective n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleUsesDirective n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ModuleOpensDirective n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(UnparsableStmt n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ReceiverParameter n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(VarType n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(Modifier n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(SwitchExpr n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(YieldStmt n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(TextBlockLiteralExpr n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(PatternExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(TypeParameter n, MOPParameters arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public MOPParameters visit(ClassOrInterfaceDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(RecordDeclaration n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(CompactConstructorDeclaration n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(EnumDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getEntries(), arg);

		return ret;
	}

	public MOPParameters visit(EnumConstantDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getArgs(), arg);
		process(ret, n.getClassBody(), arg);

		return ret;
	}

	public MOPParameters visit(AnnotationDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);

		return ret;
	}

	public MOPParameters visit(AnnotationMemberDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getDefaultValue(), arg);

		return ret;
	}

	public MOPParameters visit(FieldDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getVariables(), arg);

		return ret;
	}

	public MOPParameters visit(VariableDeclarator n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getInit(), arg);

		return ret;
	}

	public MOPParameters visit(ConstructorDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getParameters(), arg);
		process(ret, n.getThrows(), arg);
		process(ret, n.getBlock(), arg);

		return ret;
	}

	public MOPParameters visit(MethodDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getParameters(), arg);
		process(ret, n.getThrows(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(Parameter n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);

		return ret;
	}

	public MOPParameters visit(InitializerDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getBlock(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(JavadocComment n, MOPParameters arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public MOPParameters visit(ClassOrInterfaceType n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(PrimitiveType n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ArrayType n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(ArrayCreationLevel n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(IntersectionType n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(UnionType n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ReferenceType n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(VoidType n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(WildcardType n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(UnknownType n, MOPParameters arg) {
		return null;
	}

	// - Expression ----------------------------------------

	public MOPParameters visit(ArrayAccessExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getName(), arg);
		process(ret, n.getIndex(), arg);

		return ret;
	}

	public MOPParameters visit(ArrayCreationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getInitializer(), arg);
		process(ret, n.getDimensions(), arg);

		return ret;
	}

	public MOPParameters visit(ArrayInitializerExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getValues(), arg);

		return ret;
	}

	public MOPParameters visit(AssignExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getTarget(), arg);
		process(ret, n.getValue(), arg);

		return ret;
	}

	public MOPParameters visit(BinaryExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getLeft(), arg);
		process(ret, n.getRight(), arg);

		return ret;
	}

	public MOPParameters visit(CastExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);

		return ret;
	}

	public MOPParameters visit(ClassExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ConditionalExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getCondition(), arg);
		process(ret, n.getThenExpr(), arg);
		process(ret, n.getElseExpr(), arg);

		return ret;
	}

	public MOPParameters visit(EnclosedExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getInner(), arg);

		return ret;
	}

	public MOPParameters visit(FieldAccessExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(InstanceOfExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);

		return ret;
	}

	public MOPParameters visit(StringLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(IntegerLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(LongLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(CharLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(DoubleLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(BooleanLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(NullLiteralExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(MethodCallExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getArgs(), arg);

		return ret;
	}

	public MOPParameters visit(NameExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		if (arg.getParam(n.getName()) != null) {
			ret.add(arg.getParam(n.getName()));
		}

		return ret;
	}

	public MOPParameters visit(ObjectCreationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getArgs(), arg);
		process(ret, n.getAnonymousClassBody(), arg);

		return ret;
	}

	public MOPParameters visit(QualifiedNameExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getQualifier(), arg);

		return ret;
	}

	public MOPParameters visit(ThisExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getClassExpr(), arg);

		return ret;
	}

	public MOPParameters visit(SuperExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getClassExpr(), arg);

		return ret;
	}

	public MOPParameters visit(UnaryExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);

		return ret;
	}

	public MOPParameters visit(VariableDeclarationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getVars(), arg);

		return ret;
	}

	public MOPParameters visit(MarkerAnnotationExpr n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(SingleMemberAnnotationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getMemberValue(), arg);

		return ret;
	}

	public MOPParameters visit(NormalAnnotationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getPairs(), arg);

		return ret;
	}

	public MOPParameters visit(MemberValuePair n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getValue(), arg);

		return ret;
	}

	// - Statements ----------------------------------------

	public MOPParameters visit(ExplicitConstructorInvocationStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);
		process(ret, n.getArgs(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(LocalClassDeclarationStmt n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(LocalRecordDeclarationStmt n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(AssertStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getCheck(), arg);
		process(ret, n.getMessage(), arg);

		return ret;
	}

	public MOPParameters visit(BlockStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getStmts(), arg);

		return ret;
	}

	public MOPParameters visit(LabeledStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getStmt(), arg);

		return ret;
	}

	public MOPParameters visit(EmptyStmt n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ExpressionStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);

		return ret;
	}

	public MOPParameters visit(SwitchStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getSelector(), arg);
		process(ret, n.getEntries(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(SwitchEntry n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getLabel(), arg);
		process(ret, n.getStmts(), arg);

		return ret;
	}

	public MOPParameters visit(BreakStmt n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(ReturnStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);

		return ret;
	}

	public MOPParameters visit(IfStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getCondition(), arg);
		process(ret, n.getThenStmt(), arg);
		process(ret, n.getElseStmt(), arg);

		return ret;
	}

	public MOPParameters visit(WhileStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getCondition(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(ContinueStmt n, MOPParameters arg) {
		return null;
	}

	public MOPParameters visit(DoStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getBody(), arg);
		process(ret, n.getCondition(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(ForEachStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getVariable(), arg);
		process(ret, n.getIterable(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(ForStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getInit(), arg);
		process(ret, n.getCompare(), arg);
		process(ret, n.getUpdate(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(ThrowStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);

		return ret;
	}

	public MOPParameters visit(SynchronizedStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpr(), arg);
		process(ret, n.getBlock(), arg);

		return ret;
	}

	public MOPParameters visit(TryStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getTryBlock(), arg);
		process(ret, n.getCatchs(), arg);
		process(ret, n.getFinallyBlock(), arg);

		return ret;
	}

	public MOPParameters visit(CatchClause n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExcept(), arg);
		process(ret, n.getCatchBlock(), arg);

		return ret;
	}

	@Override
	public MOPParameters visit(LambdaExpr n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(MethodReferenceExpr n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(TypeExpr n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(NodeList n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(Name n, MOPParameters arg) {
		return null;
	}

	@Override
	public MOPParameters visit(SimpleName n, MOPParameters arg) {
		return null;
	}

}
