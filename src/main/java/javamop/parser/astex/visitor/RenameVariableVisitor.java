// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.parser.astex.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javamop.parser.ast.CompilationUnit;
import javamop.parser.ast.ImportDeclaration;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.Node;
import javamop.parser.ast.PackageDeclaration;
import javamop.parser.ast.TypeParameter;
import javamop.parser.ast.aspectj.ArgsPointCut;
import javamop.parser.ast.aspectj.BaseTypePattern;
import javamop.parser.ast.aspectj.CFlowPointCut;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.CombinedTypePattern;
import javamop.parser.ast.aspectj.ConditionPointCut;
import javamop.parser.ast.aspectj.CountCondPointCut;
import javamop.parser.ast.aspectj.EndObjectPointCut;
import javamop.parser.ast.aspectj.EndProgramPointCut;
import javamop.parser.ast.aspectj.EndThreadPointCut;
import javamop.parser.ast.aspectj.FieldPattern;
import javamop.parser.ast.aspectj.FieldPointCut;
import javamop.parser.ast.aspectj.IDPointCut;
import javamop.parser.ast.aspectj.IFPointCut;
import javamop.parser.ast.aspectj.MethodPattern;
import javamop.parser.ast.aspectj.MethodPointCut;
import javamop.parser.ast.aspectj.NotPointCut;
import javamop.parser.ast.aspectj.NotTypePattern;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.StartThreadPointCut;
import javamop.parser.ast.aspectj.TargetPointCut;
import javamop.parser.ast.aspectj.ThisPointCut;
import javamop.parser.ast.aspectj.ThreadBlockedPointCut;
import javamop.parser.ast.aspectj.ThreadNamePointCut;
import javamop.parser.ast.aspectj.ThreadPointCut;
import javamop.parser.ast.aspectj.TypePattern;
import javamop.parser.ast.aspectj.WildcardParameter;
import javamop.parser.ast.aspectj.WithinPointCut;
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
import javamop.parser.ast.expr.ArrayAccessExpr;
import javamop.parser.ast.expr.ArrayCreationExpr;
import javamop.parser.ast.expr.ArrayInitializerExpr;
import javamop.parser.ast.expr.AssignExpr;
import javamop.parser.ast.expr.BinaryExpr;
import javamop.parser.ast.expr.BooleanLiteralExpr;
import javamop.parser.ast.expr.CastExpr;
import javamop.parser.ast.expr.CharLiteralExpr;
import javamop.parser.ast.expr.ClassExpr;
import javamop.parser.ast.expr.ConditionalExpr;
import javamop.parser.ast.expr.DoubleLiteralExpr;
import javamop.parser.ast.expr.EnclosedExpr;
import javamop.parser.ast.expr.Expression;
import javamop.parser.ast.expr.FieldAccessExpr;
import javamop.parser.ast.expr.InstanceOfExpr;
import javamop.parser.ast.expr.IntegerLiteralExpr;
import javamop.parser.ast.expr.IntegerLiteralMinValueExpr;
import javamop.parser.ast.expr.LongLiteralExpr;
import javamop.parser.ast.expr.LongLiteralMinValueExpr;
import javamop.parser.ast.expr.MarkerAnnotationExpr;
import javamop.parser.ast.expr.MemberValuePair;
import javamop.parser.ast.expr.MethodCallExpr;
import javamop.parser.ast.expr.NameExpr;
import javamop.parser.ast.expr.NormalAnnotationExpr;
import javamop.parser.ast.expr.NullLiteralExpr;
import javamop.parser.ast.expr.ObjectCreationExpr;
import javamop.parser.ast.expr.QualifiedNameExpr;
import javamop.parser.ast.expr.SingleMemberAnnotationExpr;
import javamop.parser.ast.expr.StringLiteralExpr;
import javamop.parser.ast.expr.SuperExpr;
import javamop.parser.ast.expr.SuperMemberAccessExpr;
import javamop.parser.ast.expr.ThisExpr;
import javamop.parser.ast.expr.UnaryExpr;
import javamop.parser.ast.expr.VariableDeclarationExpr;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.Formula;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.PropertyAndHandlers;
import javamop.parser.ast.stmt.AssertStmt;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.ast.stmt.BreakStmt;
import javamop.parser.ast.stmt.CatchClause;
import javamop.parser.ast.stmt.ContinueStmt;
import javamop.parser.ast.stmt.DoStmt;
import javamop.parser.ast.stmt.EmptyStmt;
import javamop.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import javamop.parser.ast.stmt.ExpressionStmt;
import javamop.parser.ast.stmt.ForStmt;
import javamop.parser.ast.stmt.ForeachStmt;
import javamop.parser.ast.stmt.IfStmt;
import javamop.parser.ast.stmt.LabeledStmt;
import javamop.parser.ast.stmt.ReturnStmt;
import javamop.parser.ast.stmt.SwitchEntryStmt;
import javamop.parser.ast.stmt.SwitchStmt;
import javamop.parser.ast.stmt.SynchronizedStmt;
import javamop.parser.ast.stmt.ThrowStmt;
import javamop.parser.ast.stmt.TryStmt;
import javamop.parser.ast.stmt.TypeDeclarationStmt;
import javamop.parser.ast.stmt.WhileStmt;
import javamop.parser.ast.type.ClassOrInterfaceType;
import javamop.parser.ast.type.PrimitiveType;
import javamop.parser.ast.type.ReferenceType;
import javamop.parser.ast.type.VoidType;
import javamop.parser.ast.type.WildcardType;

public class RenameVariableVisitor implements javamop.parser.ast.visitor.GenericVisitor<Node, HashMap<String, MOPParameter>> {

	@Override
	public Node visit(Node n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(MOPSpecFile f, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(JavaMOPSpec s, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(MOPParameter p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getName());
		
		if(param != null)
			return new MOPParameter(p.getBeginLine(), p.getBeginColumn(), p.getType(), param.getName());

		return p;
	}

	@Override
	public Node visit(EventDefinition e, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(PropertyAndHandlers p, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(Formula f, HashMap<String, MOPParameter> arg) {
		return null;
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
		
		return new ArgsPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), list);
	}

	@Override
	public Node visit(CombinedPointCut p, HashMap<String, MOPParameter> arg) {
		List<PointCut> pointcuts = new ArrayList<PointCut>();
		
		for(PointCut p2 : p.getPointcuts()){
			PointCut p3 = (PointCut)p2.accept(this, arg);
			
			pointcuts.add(p3);
		}
		
		return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
	}

	@Override
	public Node visit(NotPointCut p, HashMap<String, MOPParameter> arg) {
		PointCut sub = (PointCut)p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new NotPointCut(p.getBeginLine(), p.getBeginColumn(), sub);
	}

	@Override
	public Node visit(ConditionPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new ConditionPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), expr);
	}
	
	@Override
	public Node visit(CountCondPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new CountCondPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), expr);
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
		
		return new TargetPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), target);
	}

	@Override
	public Node visit(ThisPointCut p, HashMap<String, MOPParameter> arg) {
		TypePattern target = (TypePattern)p.getTarget().accept(this, arg);
		
		if(p.getTarget() == target)
			return p;
		
		return new ThisPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), target);
	}

	@Override
	public Node visit(CFlowPointCut p, HashMap<String, MOPParameter> arg) {
		PointCut sub = (PointCut)p.getPointCut().accept(this, arg);
		
		if(p.getPointCut() == sub)
			return p;
		
		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), sub);
	}

	@Override
	public Node visit(IFPointCut p, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)p.getExpression().accept(this, arg);
		
		if(p.getExpression() == expr)
			return p;
		
		return new IFPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), expr);
	}

	@Override
	public Node visit(IDPointCut p, HashMap<String, MOPParameter> arg) {
		List<TypePattern> list = new ArrayList<TypePattern>();

		for(int i = 0; i < p.getArgs().size(); i++){
			TypePattern type = p.getArgs().get(i);
			
			list.add((TypePattern)type.accept(this, arg));
		}
		
		return new IDPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), list);
	}

	@Override
	public Node visit(WithinPointCut p, HashMap<String, MOPParameter> arg) {
		return p;
	}

	@Override
	public Node visit(ThreadPointCut p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getId());
		
		if(param != null)
			return new ThreadPointCut(p.getBeginLine(), p.getBeginColumn(), param.getName());

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
			return new EndObjectPointCut(p.getBeginLine(), p.getBeginColumn(), p.getTargetType(), param.getName());

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

		return new CombinedTypePattern(p.getBeginLine(), p.getBeginColumn(), p.getOp(), subTypes);
	}

	@Override
	public Node visit(NotTypePattern p, HashMap<String, MOPParameter> arg) {
		TypePattern type = (TypePattern)p.getType().accept(this, arg);
		
		if(p.getType() == type)
			return p;
		
		return new NotTypePattern(p.getBeginLine(), p.getBeginColumn(), type);
	}

	@Override
	public Node visit(BaseTypePattern p, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(p.getOp());
		
		if(param != null)
			return new BaseTypePattern(p.getBeginLine(), p.getBeginColumn(), param.getName());

		return p;
	}

	@Override
	public Node visit(CompilationUnit n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(PackageDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ImportDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(TypeParameter n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ClassOrInterfaceDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(EnumDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(EmptyTypeDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(EnumConstantDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(AnnotationDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(AnnotationMemberDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(FieldDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(VariableDeclarator n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(VariableDeclaratorId n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ConstructorDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(MethodDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(Parameter n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(EmptyMemberDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(InitializerDeclaration n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ClassOrInterfaceType n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(PrimitiveType n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ReferenceType n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(VoidType n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(WildcardType n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ArrayAccessExpr n, HashMap<String, MOPParameter> arg) {
		Expression name = (Expression)n.getName().accept(this, arg);
		Expression index = (Expression)n.getIndex().accept(this, arg);

		if(n.getName() == name && n.getIndex() == index)
			return n;

		return new ArrayAccessExpr(n.getBeginLine(), n.getBeginColumn(), name, index);
	}

	@Override
	public Node visit(ArrayCreationExpr n, HashMap<String, MOPParameter> arg) {
		if(n.getDimensions() != null){
			List<Expression> dims = new ArrayList<Expression>();
	
			for(int i = 0; i < n.getDimensions().size(); i++){
				dims.add((Expression)n.getDimensions().get(i).accept(this, arg));
			}
			
			return new ArrayCreationExpr(n.getBeginLine(), n.getBeginColumn(), n.getType(), n.getTypeArgs(), dims, n.getArrayCount());
		} if(n.getInitializer() != null){
			ArrayInitializerExpr initializer = (ArrayInitializerExpr)n.getInitializer().accept(this, arg);
			
			if(n.getInitializer() == initializer)
				return n;
			
			return new ArrayCreationExpr(n.getBeginLine(), n.getBeginColumn(), n.getType(), n.getTypeArgs(), n.getArrayCount(), initializer);
		}
		
		return n;
	}

	@Override
	public Node visit(ArrayInitializerExpr n, HashMap<String, MOPParameter> arg) {
		List<Expression> values = new ArrayList<Expression>();
		
		for(int i = 0; i < n.getValues().size(); i++){
			values.add((Expression)n.getValues().get(i).accept(this, arg));
		}
		
		return new ArrayInitializerExpr(n.getBeginLine(), n.getBeginColumn(), values);
	}

	@Override
	public Node visit(AssignExpr n, HashMap<String, MOPParameter> arg) {
		Expression target = (Expression)n.getTarget().accept(this, arg);
		Expression value = (Expression)n.getValue().accept(this, arg);

		if(n.getTarget() == target && n.getValue() == value)
			return n;

		return new AssignExpr(n.getBeginLine(), n.getBeginColumn(), target, value, n.getOperator());
	}

	@Override
	public Node visit(BinaryExpr n, HashMap<String, MOPParameter> arg) {
		Expression left = (Expression)n.getLeft().accept(this, arg);
		Expression right = (Expression)n.getRight().accept(this, arg);

		if(n.getLeft() == left && n.getRight() == right)
			return n;
		
		return new BinaryExpr(n.getBeginLine(), n.getBeginColumn(), left, right, n.getOperator());
	}

	@Override
	public Node visit(CastExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.getExpr().accept(this, arg);
		
		if(n.getExpr() == expr)
			return n;
		
		return new CastExpr(n.getBeginLine(), n.getBeginColumn(), n.getType(), expr);
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
		
		return new ConditionalExpr(n.getBeginLine(), n.getBeginColumn(), condition, thenExpr, elseExpr);
	}

	@Override
	public Node visit(EnclosedExpr n, HashMap<String, MOPParameter> arg) {
		Expression inner = (Expression)n.getInner().accept(this, arg);

		if(n.getInner() == inner)
			return n;
		
		return new EnclosedExpr(n.getBeginLine(), n.getBeginColumn(), inner);
	}

	@Override
	public Node visit(FieldAccessExpr n, HashMap<String, MOPParameter> arg) {
		Expression scope = (Expression)n.getScope().accept(this, arg);
		
		if(n.getScope() == scope)
			return n;
		
		return new FieldAccessExpr(n.getBeginLine(), n.getBeginColumn(), scope, n.getTypeArgs(), n.getField());
	}

	@Override
	public Node visit(InstanceOfExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.getExpr().accept(this, arg);
		
		if(n.getExpr() == expr)
			return n;

		return new InstanceOfExpr(n.getBeginLine(), n.getBeginColumn(), n.getExpr(), n.getType());
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
	public Node visit(IntegerLiteralMinValueExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(LongLiteralMinValueExpr n, HashMap<String, MOPParameter> arg) {
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
		Expression scope = (Expression)n.getScope().accept(this, arg);
		
		List<Expression> args = new ArrayList<Expression>();

		for(int i = 0; i < n.getArgs().size(); i++){
			args.add((Expression)n.getArgs().get(i).accept(this, arg));
		}

		return new MethodCallExpr(n.getBeginLine(), n.getBeginColumn(), scope, n.getTypeArgs(), n.getName(), args);
	}

	@Override
	public Node visit(NameExpr n, HashMap<String, MOPParameter> arg) {
		MOPParameter param = arg.get(n.getName());

		if(param != null)
			return new NameExpr(n.getBeginLine(), n.getBeginColumn(), param.getName());
		
		return n;
	}

	@Override
	public Node visit(ObjectCreationExpr n, HashMap<String, MOPParameter> arg) {
		Expression scope = (Expression)n.getScope().accept(this, arg);
		
		List<Expression> args = new ArrayList<Expression>();

		for(int i = 0; i < n.getArgs().size(); i++){
			args.add((Expression)n.getArgs().get(i).accept(this, arg));
		}

		return new ObjectCreationExpr(n.getBeginLine(), n.getBeginColumn(), scope, n.getType(), n.getTypeArgs(), args, n.getAnonymousClassBody());

	}

	@Override
	public Node visit(QualifiedNameExpr n, HashMap<String, MOPParameter> arg) {
		NameExpr qualifier = (NameExpr)n.getQualifier().accept(this, arg);
		MOPParameter param = arg.get(n.getName());		
		
		if(n.getQualifier() == qualifier && param == null)
			return n;
		
		return new QualifiedNameExpr(n.getBeginLine(), n.getBeginColumn(), qualifier, param.getName());
	}

	@Override
	public Node visit(SuperMemberAccessExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(ThisExpr n, HashMap<String, MOPParameter> arg) {
		Expression classExpr = (Expression)n.getClassExpr().accept(this, arg);
		
		if(n.getClassExpr() == classExpr)
			return n;

		return new ThisExpr(n.getBeginLine(), n.getBeginColumn(), classExpr);
	}

	@Override
	public Node visit(SuperExpr n, HashMap<String, MOPParameter> arg) {
		Expression classExpr = (Expression)n.getClassExpr().accept(this, arg);
		
		if(n.getClassExpr() == classExpr)
			return n;

		return new SuperExpr(n.getBeginLine(), n.getBeginColumn(), classExpr);
	}

	@Override
	public Node visit(UnaryExpr n, HashMap<String, MOPParameter> arg) {
		Expression expr = (Expression)n.getExpr().accept(this, arg);
		
		if(n.getExpr() == expr)
			return n;

		return new UnaryExpr(n.getBeginLine(), n.getBeginColumn(), expr, n.getOperator());

	}

	@Override
	public Node visit(VariableDeclarationExpr n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(MarkerAnnotationExpr n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(SingleMemberAnnotationExpr n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(NormalAnnotationExpr n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(MemberValuePair n, HashMap<String, MOPParameter> arg) {
		return null;
	}

	@Override
	public Node visit(ExplicitConstructorInvocationStmt n, HashMap<String, MOPParameter> arg) {
		return n;
	}

	@Override
	public Node visit(TypeDeclarationStmt n, HashMap<String, MOPParameter> arg) {
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
	public Node visit(SwitchEntryStmt n, HashMap<String, MOPParameter> arg) {
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
	public Node visit(ForeachStmt n, HashMap<String, MOPParameter> arg) {
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

