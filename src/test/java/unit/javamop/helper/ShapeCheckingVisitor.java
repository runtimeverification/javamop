package javamop.helper;

import javamop.parser.ast.*;
import javamop.parser.ast.aspectj.*;
import javamop.parser.ast.body.*;
import javamop.parser.ast.expr.*;
import javamop.parser.ast.mopspec.*;
import javamop.parser.ast.stmt.*;
import javamop.parser.ast.type.*;
import javamop.parser.ast.visitor.GenericVisitor;

import java.util.List;

/**
 * Created by He Xiao on 3/20/2016.
 * The shape analysis checks whether two ast have the 'same' structure
 * without comparing concrete values at each node.
 */
public class ShapeCheckingVisitor implements GenericVisitor<Boolean, Node> {
    @Override
    public Boolean visit(Node n, Node arg) {
        return true;
    }

    /**
     * Check whether the mop spec file f has the same shape as arg.
     *
     * @param f
     * @param arg
     * @return Whether nodes f and arg have the same shape.
     */
    @Override
    public Boolean visit(MOPSpecFile f, Node arg) {
        if (!(arg instanceof MOPSpecFile))
            return false;

        if (f == arg)
            return true;
        if (f == null || arg == null)
            return false;

        //neither f nor arg is null
        MOPSpecFile other = (MOPSpecFile) arg;

        PackageDeclaration packageDeclaration = f.getPakage();
        List<ImportDeclaration> importDeclarationList = f.getImports();
        List<JavaMOPSpec> specs = f.getSpecs();

        PackageDeclaration packageDeclaration2 = other.getPakage();
        List<ImportDeclaration> importDeclarationList2 = other.getImports();
        List<JavaMOPSpec> specs2 = other.getSpecs();

        if (!visit(packageDeclaration, packageDeclaration2))
            return false;

        if (importDeclarationList.size() != importDeclarationList2.size()
                || specs.size() != specs2.size())
            return false;

        //compare each import stmts
        for (int i = 0; i < importDeclarationList.size(); i++) {
            ImportDeclaration importDeclaration = importDeclarationList.get(i);
            ImportDeclaration importDeclaration2 = importDeclarationList2.get(i);

            if (!visit(importDeclaration, importDeclaration2))
                return false;
        }

        //compare each javamop spec contained in the mop spec file.
        for (int i = 0; i < specs.size(); i++) {
            JavaMOPSpec spec = specs.get(i);
            JavaMOPSpec spec2 = specs2.get(i);
            if (!visit(spec, spec2))
                return false;
        }

        return true;
    }

    @Override
    public Boolean visit(JavaMOPSpec s, Node arg) {
        //TODO

        return true;
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
        if (!(arg instanceof PackageDeclaration))
            return false;

        if (n == arg) {
            return true;
        }

        if (n == null || arg == null)
            return false;

        PackageDeclaration other = (PackageDeclaration) arg;
        NameExpr ne1 = n.getName();
        NameExpr ne2 = other.getName();

        if (!visit(ne1, ne2))
            return false;

        List<AnnotationExpr> annotationExprs1 = n.getAnnotations();
        List<AnnotationExpr> annotationExprs2 = other.getAnnotations();

        if (annotationExprs1.size() != annotationExprs2.size())
            return false;

        for (int i = 0; i < annotationExprs1.size(); i++) {
            if (!annotationExprs1.get(i).equals(annotationExprs2.get(i))) {
               return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(ImportDeclaration n, Node arg) {
        if (!(arg instanceof ImportDeclaration))
            return false;

        if (n == arg) {
            return true;
        }

        if (n == null || arg == null)
            return false;

        ImportDeclaration other = (ImportDeclaration) arg;
        if (!visit(n.getName(), other.getName()))
            return false;

        return n.isAsterisk() == other.isAsterisk()
                && n.isStatic() == other.isStatic();
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
        if (!(arg instanceof NameExpr))
            return false;

        if (n == arg) {
            return true;
        }

        if (n == null || arg == null)
            return false;

        String name1 = n.getName();
        String name2 = ((NameExpr) arg).getName();

        if (name1 == name2)
            return true;

        if (name1 == null || name2 == null)
            return false;

        return name1.equals(name2);
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
