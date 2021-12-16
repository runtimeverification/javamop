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

public class RemoveThreadNameVisitor implements GenericVisitor<PointCut, Integer>{

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
				if(andType && p2 instanceof ThreadNamePointCut){
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
		return null;
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
		return p;
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

	@Override
	public PointCut visit(ModuleDeclaration n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ModuleRequiresDirective n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ModuleExportsDirective n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ModuleProvidesDirective n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ModuleUsesDirective n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ModuleOpensDirective n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(UnparsableStmt n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ReceiverParameter n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(VarType n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(Modifier n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(SwitchExpr n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(YieldStmt n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(TextBlockLiteralExpr n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(PatternExpr n, Integer arg) {
		return null;
	}

	public PointCut visit(TypeParameter n, Integer arg){
		return null;
	}

	@Override
	public PointCut visit(LineComment n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(BlockComment n, Integer arg) {
		return null;
	}

	// - Body ----------------------------------------------

	public PointCut visit(ClassOrInterfaceDeclaration n, Integer arg){
		return null;
	}

	@Override
	public PointCut visit(RecordDeclaration n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(CompactConstructorDeclaration n, Integer arg) {
		return null;
	}

	public PointCut visit(EnumDeclaration n, Integer arg){
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

	public PointCut visit(ConstructorDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(MethodDeclaration n, Integer arg){
		return null;
	}

	public PointCut visit(Parameter n, Integer arg){
		return null;
	}

	public PointCut visit(InitializerDeclaration n, Integer arg){
		return null;
	}

	@Override
	public PointCut visit(JavadocComment n, Integer arg) {
		return null;
	}

	// - Type ----------------------------------------------

	public PointCut visit(ClassOrInterfaceType n, Integer arg){
		return null;
	}

	public PointCut visit(PrimitiveType n, Integer arg){
		return null;
	}

	@Override
	public PointCut visit(ArrayType n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(ArrayCreationLevel n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(IntersectionType n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(UnionType n, Integer arg) {
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

	@Override
	public PointCut visit(UnknownType n, Integer arg) {
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

	@Override
	public PointCut visit(LocalClassDeclarationStmt n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(LocalRecordDeclarationStmt n, Integer arg) {
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

	@Override
	public PointCut visit(SwitchEntry n, Integer arg) {
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

	@Override
	public PointCut visit(ForEachStmt n, Integer arg) {
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

	@Override
	public PointCut visit(LambdaExpr n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(MethodReferenceExpr n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(TypeExpr n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(NodeList n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(Name n, Integer arg) {
		return null;
	}

	@Override
	public PointCut visit(SimpleName n, Integer arg) {
		return null;
	}
}
