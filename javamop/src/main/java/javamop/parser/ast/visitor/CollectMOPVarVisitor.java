// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.ast.visitor;


import java.util.Collection;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import javamop.parser.ast.mopspec.MOPParameters;

public class CollectMOPVarVisitor extends BaseVisitor<MOPParameters, MOPParameters> {

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

	public MOPParameters visit(ClassOrInterfaceDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);

		return ret;
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
		process(ret, n.getArguments(), arg);
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

		process(ret, n.getInitializer(), arg);

		return ret;
	}

	public MOPParameters visit(ConstructorDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getParameters(), arg);
		process(ret, n.getThrownExceptions(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(MethodDeclaration n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getParameters(), arg);
		process(ret, n.getThrownExceptions(), arg);
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

		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(ArrayAccessExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getName(), arg);
		process(ret, n.getIndex(), arg);

		return ret;
	}

	public MOPParameters visit(ArrayCreationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getInitializer(), arg);
		process(ret, n.getLevels(), arg);

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

		process(ret, n.getExpression(), arg);

		return ret;
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

	public MOPParameters visit(InstanceOfExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);

		return ret;
	}

	public MOPParameters visit(MethodCallExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getArguments(), arg);

		return ret;
	}

	public MOPParameters visit(NameExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		if (arg.getParam(n.getName().asString()) != null) {
			ret.add(arg.getParam(n.getName().asString()));
		}

		return ret;
	}

	public MOPParameters visit(ObjectCreationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getArguments(), arg);
		process(ret, n.getAnonymousClassBody(), arg);

		return ret;
	}

	public MOPParameters visit(ThisExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.asThisExpr(), arg);

		return ret;
	}

	public MOPParameters visit(SuperExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.asSuperExpr(), arg);

		return ret;
	}

	public MOPParameters visit(UnaryExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);

		return ret;
	}

	public MOPParameters visit(VariableDeclarationExpr n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getAnnotations(), arg);
		process(ret, n.getVariables(), arg);

		return ret;
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

	public MOPParameters visit(ExplicitConstructorInvocationStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);
		process(ret, n.getArguments(), arg);

		return ret;
	}

	public MOPParameters visit(AssertStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getCheck(), arg);
		process(ret, n.getMessage(), arg);

		return ret;
	}

	public MOPParameters visit(BlockStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getStatements(), arg);

		return ret;
	}

	public MOPParameters visit(LabeledStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getStatement(), arg);

		return ret;
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

		process(ret, n.getLabels(), arg);
		process(ret, n.getStatements(), arg);

		return ret;
	}

	public MOPParameters visit(ReturnStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);

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

		process(ret, n.getInitialization(), arg);
		process(ret, n.getCompare(), arg);
		process(ret, n.getUpdate(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(ThrowStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);

		return ret;
	}

	public MOPParameters visit(SynchronizedStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getExpression(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}

	public MOPParameters visit(TryStmt n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getTryBlock(), arg);
		process(ret, n.getCatchClauses(), arg);
		process(ret, n.getFinallyBlock(), arg);

		return ret;
	}

	public MOPParameters visit(CatchClause n, MOPParameters arg) {
		MOPParameters ret = new MOPParameters();

		process(ret, n.getParameter(), arg);
		process(ret, n.getBody(), arg);

		return ret;
	}
}
