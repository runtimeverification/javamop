// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.visitor.BaseVisitor;

public class RenameVariableVisitor extends BaseVisitor<Node, HashMap<String, MOPParameter>> {

	@Override
	public Node visit(MOPParameter p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getName());
		
		if(param != null)
			return new MOPParameter(p.getTokenRange().get(), p.getType(), param.getName());

		return p;
	}

	@Override
	public Node visit(WildcardParameter w, HashMap<String, MOPParameter> arg) {
		return w;
	}

	@Override
	public Node visit(ArgsPointCut p, HashMap<String, MOPParameter> arg) {
		List<TypePattern> list = new ArrayList<TypePattern>();

		for(int i = 0; i < p.getArgs().size(); i++){
			TypePattern type = p.getArgs().get(i);
			
			list.add((TypePattern)type.accept(this, arg));
		}
		
		return new ArgsPointCut(p.getTokenRange().get(), p.getType(), list);
	}

	@Override
	public Node visit(CombinedPointCut p, HashMap<String, MOPParameter> arg) {
		List<PointCut> pointcuts = new ArrayList<PointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			PointCut p3 = (PointCut)p2.accept(this, arg);
			
			pointcuts.add(p3);
		}
		
		return new CombinedPointCut(p.getTokenRange().get(), p.getType(), pointcuts);
	}

	@Override
	public Node visit(NotPointCut p, HashMap<String, MOPParameter> arg) {
		PointCut sub = (PointCut)p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new NotPointCut(p.getTokenRange().get(), sub);
	}

	@Override
	public Node visit(ConditionPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new ConditionPointCut(p.getTokenRange().get(), p.getType(), expr);
	}
	
	@Override
	public Node visit(CountCondPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new CountCondPointCut(p.getTokenRange().get(), p.getType(), expr);
	}

	@Override
	public Node visit(FieldPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(MethodPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(TargetPointCut p, HashMap<String, MOPParameter> arg) {
		TypePattern target = (TypePattern)p.getTarget().accept(this, arg);
		
		if(p.getTarget() == target)
			return p;
		
		return new TargetPointCut(p.getTokenRange().get(), p.getType(), target);
	}

	@Override
	public Node visit(ThisPointCut p, HashMap<String, MOPParameter> arg) {
		TypePattern target = (TypePattern)p.getTarget().accept(this, arg);
		
		if(p.getTarget() == target)
			return p;
		
		return new ThisPointCut(p.getTokenRange().get(), p.getType(), target);
	}

	@Override
	public Node visit(CFlowPointCut p, HashMap<String, MOPParameter> arg) {
		PointCut sub = (PointCut)p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new CFlowPointCut(p.getTokenRange().get(), p.getType(), sub);
	}

	@Override
	public Node visit(IFPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new IFPointCut(p.getTokenRange().get(), p.getType(), expr);
	}

	@Override
	public Node visit(IDPointCut p, HashMap<String, MOPParameter> arg) {
		List<TypePattern> list = new ArrayList<TypePattern>();

		for(int i = 0; i < p.getArgs().size(); i++){
			TypePattern type = p.getArgs().get(i);
			
			list.add((TypePattern)type.accept(this, arg));
		}
		
		return new IDPointCut(p.getTokenRange().get(), p.getType(), list);
	}

	@Override
	public Node visit(WithinPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(ThreadPointCut p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getId());
		
		if(param != null)
			return new ThreadPointCut(p.getTokenRange().get(), param.getName());

		return p;
	}
	
	@Override
	public Node visit(ThreadNamePointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}
	
	@Override
	public Node visit(ThreadBlockedPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(EndProgramPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(EndThreadPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(EndObjectPointCut p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getId());
		
		if(param != null)
			return new EndObjectPointCut(p.getTokenRange().get(), p.getTargetType(), param.getName());

		return p;
	}

	@Override
	public Node visit(StartThreadPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(FieldPattern p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(MethodPattern p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(CombinedTypePattern p, HashMap<String, MOPParameter> arg) {
		List<TypePattern> subTypes = new ArrayList<TypePattern>();

		for(int i = 0; i < p.getSubTypes().size(); i++){
			subTypes.add((TypePattern)p.getSubTypes().get(i).accept(this, arg));
		}

		return new CombinedTypePattern(p.getTokenRange().get(), p.getOp(), subTypes);
	}

	@Override
	public Node visit(NotTypePattern p, HashMap<String, MOPParameter> arg) {
		TypePattern type = (TypePattern)p.getType().accept(this, arg);
		
		if(p.getType() == type)
			return p;
		
		return new NotTypePattern(p.getTokenRange().get(), type);
	}

	@Override
	public Node visit(BaseTypePattern p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getOp());
		
		if(param != null)
			return new BaseTypePattern(p.getTokenRange().get(), param.getName());

		return p;
	}

	@Override
	public Node visit(ArrayAccessExpr n, HashMap<String, MOPParameter> arg) {
		Expression name = (Expression)n.getName().accept(this, arg);
		Expression index = (Expression)n.getIndex().accept(this, arg);

		if(n.getName() == name && n.getIndex() == index)
			return n;

		return new ArrayAccessExpr(name, index);
	}

	@Override
	public Node visit(ArrayCreationExpr n, HashMap<String, MOPParameter> arg) {
		if(n.getLevels() != null){
			List<Expression> dims = new ArrayList<>();
	
			for(int i = 0; i < n.getLevels().size(); i++){
				dims.add((Expression)n.getLevels().get(i).accept(this, arg));
			}
			return new ArrayCreationExpr(n.getElementType());
		} if(n.getInitializer() != null){
			ArrayInitializerExpr initializer = (ArrayInitializerExpr)n.getInitializer().get().asArrayInitializerExpr().accept(this, arg);

			if(n.getInitializer().equals(initializer)) {
				return n;
			}
			return new ArrayCreationExpr(n.getElementType());
		}
		
		return n;
	}

	@Override
	public Node visit(ArrayInitializerExpr n, HashMap<String, MOPParameter> arg) {
		NodeList<Expression> values = new NodeList<>();
		
		for(int i = 0; i < n.getValues().size(); i++){
			values.add((Expression)n.getValues().get(i).accept(this, arg));
		}
		
		return new ArrayInitializerExpr(values);
	}

	@Override
	public Node visit(AssignExpr n, HashMap<String, MOPParameter> arg) {
		Expression target = (Expression)n.getTarget().accept(this, arg);
		Expression value = (Expression)n.getValue().accept(this, arg);

		if(n.getTarget() == target && n.getValue() == value)
			return n;

		return new AssignExpr( target, value, n.getOperator());
	}

	@Override
	public Node visit(BinaryExpr n, HashMap<String, MOPParameter> arg) {
		Expression left = (Expression)n.getLeft().accept(this, arg);
		Expression right = (Expression)n.getRight().accept(this, arg);

		if(n.getLeft() == left && n.getRight() == right)
			return n;
		
		return new BinaryExpr(left, right, n.getOperator());
	}

	@Override
	public Node visit(CastExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.getExpression().accept(this, arg);
		
		if(n.getExpression() == expr)
			return n;
		
		return new CastExpr(n.getType(), expr);
	}

	@Override
	public Node visit(ClassExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ConditionalExpr n, HashMap<String, MOPParameter> arg) {
		Expression condition = (Expression)n.getCondition().accept(this, arg);
		Expression thenExpr = (Expression)n.getThenExpr().accept(this, arg);
		Expression elseExpr = (Expression)n.getElseExpr().accept(this, arg);
		
		if(n.getCondition() == condition && n.getThenExpr() == thenExpr && n.getElseExpr() == elseExpr)
			return n;
		
		return new ConditionalExpr(condition, thenExpr, elseExpr);
	}

	@Override
	public Node visit(EnclosedExpr n, HashMap<String, MOPParameter> arg) {
		Expression inner = (Expression)n.getInner().accept(this, arg);

		if(n.getInner() == inner)
			return n;
		
		return new EnclosedExpr(inner);
	}

	@Override
	public Node visit(FieldAccessExpr n, HashMap<String, MOPParameter> arg) {
		Expression scope = (Expression)n.getScope().accept(this, arg);
		
		if(n.getScope() == scope)
			return n;
		return new FieldAccessExpr(scope, n.getTypeArguments().get(), n.getName());
	}

	@Override
	public Node visit(InstanceOfExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.getExpression().accept(this, arg);
		
		if(n.getExpression() == expr)
			return n;

		return new InstanceOfExpr(n.getExpression(), n.getType());
	}

	@Override
	public Node visit(StringLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(IntegerLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(LongLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(CharLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(DoubleLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(BooleanLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(NullLiteralExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(MethodCallExpr n, HashMap<String, MOPParameter> arg) {
		Expression scope = (Expression)n.getScope().get().accept(this, arg);
		
		NodeList<Expression> args = new NodeList<>();

		for(int i = 0; i < n.getArguments().size(); i++){
			args.add((Expression)n.getArguments().get(i).accept(this, arg));
		}

		return new MethodCallExpr(scope, n.getTypeArguments().get(), n.getName().asString(), args);
	}

	@Override
	public Node visit(com.github.javaparser.ast.expr.NameExpr n, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(n.getName());

		if(param != null)
			return new com.github.javaparser.ast.expr.NameExpr(param.getName());
		
		return n;
	}

	@Override
	public Node visit(ObjectCreationExpr n, HashMap<String, MOPParameter> arg) {
		Expression scope = (Expression)n.getScope().get().accept(this, arg);

		NodeList<Expression> args = new NodeList<>();

		for(int i = 0; i < n.getArguments().size(); i++){
			args.add((Expression)n.getArguments().get(i).accept(this, arg));
		}

		return new ObjectCreationExpr(scope, n.getType(), n.getTypeArguments().get(), args, n.getAnonymousClassBody().get());

	}

//	@Override
//	public Node visit(QualifiedNameExpr n, HashMap<String, MOPParameter> arg) {
//		NameExpr qualifier = (NameExpr)n.getQualifier().accept(this, arg);
//		MOPParameter param = arg.get(n.getName());
//
//		if(n.getQualifier().equals(qualifier) && param == null)
//			return n;
//
//		return new QualifiedNameExpr(n.getBeginLine(), n.getBeginColumn(), qualifier, param.getName());
//	}

	@Override
	public Node visit(ThisExpr n, HashMap<String, MOPParameter> arg) {
		Expression classExpr = (Expression)n.asThisExpr().accept(this, arg);
		
		if(n.asThisExpr() == classExpr)
			return n;

		return new ThisExpr(n.getTypeName().get());
	}

	@Override
	public Node visit(SuperExpr n, HashMap<String, MOPParameter> arg) {
		Expression classExpr = (Expression)n.asSuperExpr().accept(this, arg);
		
		if(n.asSuperExpr() == classExpr)
			return n;

		return new SuperExpr(n.getTypeName().get());
	}

	@Override
	public Node visit(UnaryExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.asUnaryExpr().accept(this, arg);
		
		if(n.asUnaryExpr() == expr)
			return n;

		return new UnaryExpr(expr, n.getOperator());

	}

	@Override
	public Node visit(VariableDeclarationExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ExplicitConstructorInvocationStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(AssertStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(BlockStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(LabeledStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(EmptyStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ExpressionStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(SwitchStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(SwitchEntry n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(BreakStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ReturnStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(IfStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(WhileStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ContinueStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(DoStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ForEachStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ForStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ThrowStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(SynchronizedStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(TryStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(CatchClause n, HashMap<String, MOPParameter> arg) {
		return n;
	}
}

