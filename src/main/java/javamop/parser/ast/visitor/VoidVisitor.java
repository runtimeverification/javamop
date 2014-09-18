// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
/*
 * Copyright (C) 2008 Feng Chen.
 * 
 * This file is part of JavaMOP parser.
 *
 * JavaMOP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaMOP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaMOP.  If not, see <http://www.gnu.org/licenses/>.
 */

package javamop.parser.ast.visitor;

import javamop.parser.ast.CompilationUnit;
import javamop.parser.ast.ImportDeclaration;
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
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.*;
import javamop.parser.ast.aspectj.*;

/**
 * @author Julio Vilmar Gesser
 */
public interface VoidVisitor<A> {

    public void visit(Node n, A arg);
    
    //- JavaMOP components
    
    public void visit(MOPSpecFile f, A arg);
    
    public void visit(JavaMOPSpec s, A arg);
    
    public void visit(MOPParameter p, A arg);
    
    public void visit(EventDefinition e, A arg);
    
    public void visit(PropertyAndHandlers p, A arg);
    
    public void visit(Formula f, A arg);
    
    //- AspectJ components --------------------
    
    public void visit(WildcardParameter w, A arg);
    
    public void visit(ArgsPointCut p, A arg);
    
    public void visit(CombinedPointCut p, A arg);
    
    public void visit(NotPointCut p, A arg);
    
    public void visit(ConditionPointCut p, A arg);
    
    public void visit(CountCondPointCut p, A arg);
    
    public void visit(FieldPointCut p, A arg);
    
    public void visit(MethodPointCut p, A arg);
    
    public void visit(TargetPointCut p, A arg);
    
    public void visit(ThisPointCut p, A arg);

    public void visit(CFlowPointCut p, A arg);

    public void visit(IFPointCut p, A arg);
    
    public void visit(IDPointCut p, A arg);

    public void visit(WithinPointCut p, A arg);

    public void visit(ThreadPointCut p, A arg);
    
    public void visit(ThreadNamePointCut p, A arg);
    
    public void visit(ThreadBlockedPointCut p, A arg);

    public void visit(EndProgramPointCut p, A arg);

    public void visit(EndThreadPointCut p, A arg);
    
    public void visit(EndObjectPointCut p, A arg);

    public void visit(StartThreadPointCut p, A arg);

    public void visit(FieldPattern p, A arg);
    
    public void visit(MethodPattern p, A arg);
    
    public void visit(CombinedTypePattern p, A arg);
    
    public void visit(NotTypePattern p, A arg);
    
    public void visit(BaseTypePattern p, A arg);

    //- Compilation Unit ----------------------------------

    public void visit(CompilationUnit n, A arg);

    public void visit(PackageDeclaration n, A arg);

    public void visit(ImportDeclaration n, A arg);

    public void visit(TypeParameter n, A arg);

    //- Body ----------------------------------------------

    public void visit(ClassOrInterfaceDeclaration n, A arg);

    public void visit(EnumDeclaration n, A arg);

    public void visit(EmptyTypeDeclaration n, A arg);

    public void visit(EnumConstantDeclaration n, A arg);

    public void visit(AnnotationDeclaration n, A arg);

    public void visit(AnnotationMemberDeclaration n, A arg);

    public void visit(FieldDeclaration n, A arg);

    public void visit(VariableDeclarator n, A arg);

    public void visit(VariableDeclaratorId n, A arg);

    public void visit(ConstructorDeclaration n, A arg);

    public void visit(MethodDeclaration n, A arg);

    public void visit(Parameter n, A arg);

    public void visit(EmptyMemberDeclaration n, A arg);

    public void visit(InitializerDeclaration n, A arg);

    //- Type ----------------------------------------------

    public void visit(ClassOrInterfaceType n, A arg);

    public void visit(PrimitiveType n, A arg);

    public void visit(ReferenceType n, A arg);

    public void visit(VoidType n, A arg);

    public void visit(WildcardType n, A arg);

    //- Expression ----------------------------------------

    public void visit(ArrayAccessExpr n, A arg);

    public void visit(ArrayCreationExpr n, A arg);

    public void visit(ArrayInitializerExpr n, A arg);

    public void visit(AssignExpr n, A arg);

    public void visit(BinaryExpr n, A arg);

    public void visit(CastExpr n, A arg);

    public void visit(ClassExpr n, A arg);

    public void visit(ConditionalExpr n, A arg);

    public void visit(EnclosedExpr n, A arg);

    public void visit(FieldAccessExpr n, A arg);

    public void visit(InstanceOfExpr n, A arg);

    public void visit(StringLiteralExpr n, A arg);

    public void visit(IntegerLiteralExpr n, A arg);

    public void visit(LongLiteralExpr n, A arg);

    public void visit(IntegerLiteralMinValueExpr n, A arg);

    public void visit(LongLiteralMinValueExpr n, A arg);

    public void visit(CharLiteralExpr n, A arg);

    public void visit(DoubleLiteralExpr n, A arg);

    public void visit(BooleanLiteralExpr n, A arg);

    public void visit(NullLiteralExpr n, A arg);

    public void visit(MethodCallExpr n, A arg);

    public void visit(NameExpr n, A arg);

    public void visit(ObjectCreationExpr n, A arg);

    public void visit(QualifiedNameExpr n, A arg);

    public void visit(SuperMemberAccessExpr n, A arg);

    public void visit(ThisExpr n, A arg);

    public void visit(SuperExpr n, A arg);

    public void visit(UnaryExpr n, A arg);
    
    public void visit(VariableDeclarationExpr n, A arg);

    public void visit(MarkerAnnotationExpr n, A arg);

    public void visit(SingleMemberAnnotationExpr n, A arg);

    public void visit(NormalAnnotationExpr n, A arg);

    public void visit(MemberValuePair n, A arg);

    //- Statements ----------------------------------------

    public void visit(ExplicitConstructorInvocationStmt n, A arg);

    public void visit(TypeDeclarationStmt n, A arg);

    public void visit(AssertStmt n, A arg);

    public void visit(BlockStmt n, A arg);

    public void visit(LabeledStmt n, A arg);

    public void visit(EmptyStmt n, A arg);

    public void visit(ExpressionStmt n, A arg);

    public void visit(SwitchStmt n, A arg);

    public void visit(SwitchEntryStmt n, A arg);

    public void visit(BreakStmt n, A arg);

    public void visit(ReturnStmt n, A arg);

    public void visit(IfStmt n, A arg);

    public void visit(WhileStmt n, A arg);

    public void visit(ContinueStmt n, A arg);

    public void visit(DoStmt n, A arg);

    public void visit(ForeachStmt n, A arg);

    public void visit(ForStmt n, A arg);

    public void visit(ThrowStmt n, A arg);

    public void visit(SynchronizedStmt n, A arg);

    public void visit(TryStmt n, A arg);

    public void visit(CatchClause n, A arg);

}
