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
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.type.ClassOrInterfaceType;
import javamop.parser.ast.type.PrimitiveType;
import javamop.parser.ast.type.ReferenceType;
import javamop.parser.ast.type.VoidType;
import javamop.parser.ast.type.WildcardType;

import java.util.ArrayList;
import java.util.List;

public class RemoveEndObjectVisitor implements GenericVisitor<PointCut, Integer>{

	public PointCut visit(Node n, Integer arg){
		return null;
	}

	// - JavaMOP components

	public PointCut visit(MOPSpecFile f, Integer arg){
		return null;
	}

	public PointCut visit(JavaMOPSpec s, Integer arg){
		return null;
	}

	public PointCut visit(MOPParameter p, Integer arg){
		return null;
	}

	public PointCut visit(EventDefinition e, Integer arg){
		return null;
	}

	public PointCut visit(PropertyAndHandlers p, Integer arg){
		return null;
	}

	public PointCut visit(Formula f, Integer arg){
		return null;
	}

	// - AspectJ components --------------------

	public PointCut visit(WildcardParameter w, Integer arg){
		return null;
	}

	public PointCut visit(ArgsPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(CombinedPointCut p, Integer arg){
		if(arg == 0){
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for(PointCut p2 : p.getPointcuts()){
				PointCut temp = p2.accept(this, new Integer(0));
				
				if(temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
		} else {
			boolean andType = (p.getType().compareTo("&&") == 0);
			boolean alreadySeen = false;
			
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			for(PointCut p2 : p.getPointcuts()){
				if(andType && p2 instanceof EndObjectPointCut){
					if(alreadySeen)
						return null;
					alreadySeen = true;
					continue;
				}
				
				PointCut temp = p2.accept(this, new Integer(0));
				
				if(temp != null)
					pointcuts.add(temp);
				else
					return null;
			}
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), pointcuts);
		}		
	}

	public PointCut visit(NotPointCut p, Integer arg){
		return new NotPointCut(p.getBeginLine(), p.getBeginColumn(), p.getPointCut().accept(this, new Integer(0)));
	}

	public PointCut visit(ConditionPointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(CountCondPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(FieldPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(MethodPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(TargetPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThisPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(CFlowPointCut p, Integer arg){
		return new CFlowPointCut(p.getBeginLine(), p.getBeginColumn(), p.getType(), p.getPointCut().accept(this, new Integer(0)));
	}

	public PointCut visit(IFPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(IDPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(WithinPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThreadPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(ThreadNamePointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(ThreadBlockedPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(EndProgramPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(EndThreadPointCut p, Integer arg){
		return p;
	}
	
	public PointCut visit(EndObjectPointCut p, Integer arg){
		if(arg == 0){
			return null;
		} else {
			List<PointCut> pointcuts = new ArrayList<PointCut>();
			return new CombinedPointCut(p.getBeginLine(), p.getBeginColumn(), "&&", pointcuts);
		}
	}

	public PointCut visit(StartThreadPointCut p, Integer arg){
		return p;
	}

	public PointCut visit(FieldPattern p, Integer arg){
		return null;
	}

	public PointCut visit(MethodPattern p, Integer arg){
		return null;
	}

	public PointCut visit(CombinedTypePattern p, Integer arg){
		return null;
	}

	public PointCut visit(NotTypePattern p, Integer arg){
		return null;
	}

	public PointCut visit(BaseTypePattern p, Integer arg){
		return null;
	}

	// - Compilation Unit ----------------------------------

	public PointCut visit(CompilationUnit n, Integer arg){
		return null;
	}

	public PointCut visit(PackageDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(ImportDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(TypeParameter n, Integer arg){
		return null;
	}

	// - Body ----------------------------------------------

	public PointCut visit(ClassOrInterfaceDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(EnumDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(EmptyTypeDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(EnumConstantDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(AnnotationDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(AnnotationMemberDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(FieldDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(VariableDeclarator n, Integer arg){
		return null;
	}

	public PointCut visit(VariableDeclaratorId n, Integer arg){
		return null;
	}

	public PointCut visit(ConstructorDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(MethodDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(Parameter n, Integer arg){
		return null;
	}

	public PointCut visit(EmptyMemberDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(InitializerDeclaration n, Integer arg){
		return null;
	}

	// - Type ----------------------------------------------

	public PointCut visit(ClassOrInterfaceType n, Integer arg){
		return null;
	}

	public PointCut visit(PrimitiveType n, Integer arg){
		return null;
	}

	public PointCut visit(ReferenceType n, Integer arg){
		return null;
	}

	public PointCut visit(VoidType n, Integer arg){
		return null;
	}

	public PointCut visit(WildcardType n, Integer arg){
		return null;
	}

	// - Expression ----------------------------------------

	public PointCut visit(ArrayAccessExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ArrayCreationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ArrayInitializerExpr n, Integer arg){
		return null;
	}

	public PointCut visit(AssignExpr n, Integer arg){
		return null;
	}

	public PointCut visit(BinaryExpr n, Integer arg){
		return null;
	}

	public PointCut visit(CastExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ClassExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ConditionalExpr n, Integer arg){
		return null;
	}

	public PointCut visit(EnclosedExpr n, Integer arg){
		return null;
	}

	public PointCut visit(FieldAccessExpr n, Integer arg){
		return null;
	}

	public PointCut visit(InstanceOfExpr n, Integer arg){
		return null;
	}

	public PointCut visit(StringLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(IntegerLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(LongLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(IntegerLiteralMinValueExpr n, Integer arg){
		return null;
	}

	public PointCut visit(LongLiteralMinValueExpr n, Integer arg){
		return null;
	}

	public PointCut visit(CharLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(DoubleLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(BooleanLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(NullLiteralExpr n, Integer arg){
		return null;
	}

	public PointCut visit(MethodCallExpr n, Integer arg){
		return null;
	}

	public PointCut visit(NameExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ObjectCreationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(QualifiedNameExpr n, Integer arg){
		return null;
	}

	public PointCut visit(SuperMemberAccessExpr n, Integer arg){
		return null;
	}

	public PointCut visit(ThisExpr n, Integer arg){
		return null;
	}

	public PointCut visit(SuperExpr n, Integer arg){
		return null;
	}

	public PointCut visit(UnaryExpr n, Integer arg){
		return null;
	}

	public PointCut visit(VariableDeclarationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(MarkerAnnotationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(SingleMemberAnnotationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(NormalAnnotationExpr n, Integer arg){
		return null;
	}

	public PointCut visit(MemberValuePair n, Integer arg){
		return null;
	}

	// - Statements ----------------------------------------

	public PointCut visit(ExplicitConstructorInvocationStmt n, Integer arg){
		return null;
	}

	public PointCut visit(TypeDeclarationStmt n, Integer arg){
		return null;
	}

	public PointCut visit(AssertStmt n, Integer arg){
		return null;
	}

	public PointCut visit(BlockStmt n, Integer arg){
		return null;
	}

	public PointCut visit(LabeledStmt n, Integer arg){
		return null;
	}

	public PointCut visit(EmptyStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ExpressionStmt n, Integer arg){
		return null;
	}

	public PointCut visit(SwitchStmt n, Integer arg){
		return null;
	}

	public PointCut visit(SwitchEntryStmt n, Integer arg){
		return null;
	}

	public PointCut visit(BreakStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ReturnStmt n, Integer arg){
		return null;
	}

	public PointCut visit(IfStmt n, Integer arg){
		return null;
	}

	public PointCut visit(WhileStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ContinueStmt n, Integer arg){
		return null;
	}

	public PointCut visit(DoStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ForeachStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ForStmt n, Integer arg){
		return null;
	}

	public PointCut visit(ThrowStmt n, Integer arg){
		return null;
	}

	public PointCut visit(SynchronizedStmt n, Integer arg){
		return null;
	}

	public PointCut visit(TryStmt n, Integer arg){
		return null;
	}

	public PointCut visit(CatchClause n, Integer arg){
		return null;
	}

}
