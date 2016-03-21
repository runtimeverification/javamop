package unit.javamop.helper;

import javamop.parser.ast.*;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.body.*;
import javamop.parser.ast.expr.*;
import javamop.parser.ast.mopspec.*;
import javamop.parser.ast.stmt.*;
import javamop.parser.ast.type.*;
import javamop.parser.ast.visitor.GenericVisitor;

/**
 * Created by He Xiao on 3/20/2016.
 */
public class ShapeCheckingVisitor implements GenericVisitor<Boolean, Node> {
    @Override
    public Boolean visit(Node n, Node arg) {
        return true;
    }

    @Override
    public Boolean visit(MOPSpecFile f, Node arg) {
        if (!(arg instanceof MOPSpecFile))
            return false;

        //TODO
        return null;
    }

    @Override
    public Boolean visit(JavaMOPSpec s, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MOPParameter p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EventDefinition e, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(PropertyAndHandlers p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(Formula f, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(WildcardParameter w, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ArgsPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CombinedPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(NotPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ConditionPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CountCondPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(FieldPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MethodPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(TargetPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThisPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CFlowPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(IFPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(IDPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(WithinPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThreadPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThreadNamePointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThreadBlockedPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EndProgramPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EndThreadPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EndObjectPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(StartThreadPointCut p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(FieldPattern p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MethodPattern p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CombinedTypePattern p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(NotTypePattern p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(BaseTypePattern p, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CompilationUnit n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(PackageDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ImportDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(TypeParameter n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ClassOrInterfaceDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EnumDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EmptyTypeDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EnumConstantDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(AnnotationDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(AnnotationMemberDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(FieldDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(VariableDeclarator n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(VariableDeclaratorId n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ConstructorDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MethodDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(Parameter n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EmptyMemberDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(InitializerDeclaration n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ClassOrInterfaceType n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(PrimitiveType n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ReferenceType n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(VoidType n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(WildcardType n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ArrayAccessExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ArrayCreationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ArrayInitializerExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(AssignExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(BinaryExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CastExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ClassExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ConditionalExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EnclosedExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(FieldAccessExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(InstanceOfExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(StringLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(IntegerLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(LongLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(IntegerLiteralMinValueExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(LongLiteralMinValueExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CharLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(DoubleLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(BooleanLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(NullLiteralExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MethodCallExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(NameExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ObjectCreationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(QualifiedNameExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SuperMemberAccessExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThisExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SuperExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(UnaryExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(VariableDeclarationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MarkerAnnotationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SingleMemberAnnotationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(NormalAnnotationExpr n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(MemberValuePair n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ExplicitConstructorInvocationStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(TypeDeclarationStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(AssertStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(BlockStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(LabeledStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(EmptyStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ExpressionStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SwitchStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SwitchEntryStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(BreakStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ReturnStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(IfStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(WhileStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ContinueStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(DoStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ForeachStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ForStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(ThrowStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(SynchronizedStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(TryStmt n, Node arg) {
        return null;
    }

    @Override
    public Boolean visit(CatchClause n, Node arg) {
        return null;
    }
}
