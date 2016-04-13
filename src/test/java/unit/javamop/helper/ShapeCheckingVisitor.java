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

    public Boolean visit(MOPParameters parameters, MOPParameters parameters2) {
        //compare parameters
        if (parameters.size() != parameters2.size())
            return false;

        for (int i = 0; i < parameters.size(); i++) {
            if (!visit(parameters.get(i), parameters2.get(i)))
                return false;
        }
        return true;
    }

    @Override
    public Boolean visit(JavaMOPSpec s, Node arg) {
        if (!(arg instanceof JavaMOPSpec))
            return false;

        if (s == arg) {
            return true;
        }

        if (s == null || arg == null)
            return false;

        JavaMOPSpec other = (JavaMOPSpec) arg;
        //compare each primitive field
        if (!s.getName().equals(other.getName()) ||
                s.getModifiers() != other.getModifiers() ||
                !s.getInMethod().equals(other.getInMethod()) ||
                !s.getRawLogic().equals(other.getRawLogic()))
            return false;

        if (!visit(s.getPackage(), other.getPackage()))
            return false;

        //all the fields are assumed to be non-null
        //compare parameters;
        if (!visit(s.getParameters(), other.getParameters()))
            return false;


        //body decl
        if (s.getDeclarations().size() != other.getDeclarations().size())
            return false;
        for (int i = 0; i < s.getDeclarations().size(); i++) {
            BodyDeclaration bodyDeclaration = s.getDeclarations().get(i);
            BodyDeclaration bodyDeclaration2 = other.getDeclarations().get(i);

            //TODO: need to evaluate this code carefully, may omit it if necessary.
            if (!bodyDeclaration.equals(bodyDeclaration2))
                return false;
        }

        //event defs
        if (s.getEvents().size() != other.getEvents().size())
            return false;

        for (int i = 0; i < s.getEvents().size(); i++) {
            EventDefinition eventDefinition = s.getEvents().get(i);
            EventDefinition eventDefinition2 = other.getEvents().get(i);
            if (!visit(eventDefinition, eventDefinition2))
                return false;
        }

        //TODO: more

        return true;
    }

    @Override
    public Boolean visit(MOPParameter p, Node arg) {
        if (!(arg instanceof MOPParameter))
            return false;

        MOPParameter other = (MOPParameter) arg;

        String name = p.getName();
        String name2 = other.getName();

        String op = p.getType().getOp();
        String op2 = other.getType().getOp();

        return name.equals(name2) && op.equals(op2);
    }

    @Override
    public Boolean visit(EventDefinition e, Node arg) {
        EventDefinition other = (EventDefinition) arg;
        boolean primitiveEq =
                e.getCondition().equals(other.getCondition())
                        && e.getCountCond().equals(other.getCountCond())
                        && e.getEndObjectVar().equals(other.getEndObjectVar())
                        && e.getId().equals(other.getId())
                        && e.getIdNum() == other.getIdNum()
                        && e.getPointCutString().equals(other.getPointCutString())
                        && e.getPos().equals(other.getPos())
                        && e.getPurePointCutString().equals(other.getPurePointCutString())
                        && e.getThreadVar().equals(other.getThreadVar())
                        && e.getUniqueId().equals(other.getUniqueId())
                        && e.getBeginColumn() == other.getBeginColumn()
                        && e.getBeginLine() == other.getBeginLine()
                        && e.getEndColumn() == other.getEndColumn()
                        && e.getEndLine() == other.getEndLine();

        if (!primitiveEq)
            return false;

        boolean boolVarsEq =
                e.has__LOC() == other.has__LOC()
                        && e.has__SKIP() == other.has__SKIP()
                        && e.has__STATICSIG() == other.has__STATICSIG()
                        && e.hasReturning() == other.hasReturning()
                        && e.hasThrowing() == other.hasThrowing()
                        && e.isBlockingEvent() == other.isBlockingEvent()
                        && e.isCreationEvent() == other.isCreationEvent()
                        && e.isEndObject() == other.isEndObject()
                        && e.isEndProgram() == other.isEndProgram()
                        && e.isEndThread() == other.isEndThread()
                        && e.isStartEvent() == other.isStartEvent()
                        && e.isStartThread() == other.isStartThread()
                        && e.isStaticEvent() == other.isStaticEvent();

        if (!boolVarsEq)
            return false;

        //compare some other sub-structures
        if (!e.getRetType().equals(other.getRetType()))
            return false;

        if (!e.getPointCut().getType().equals(other.getPointCut().getType()))
            return false;

        if (!visit(e.getParameters(), other.getParameters()))
            return false;

        if (!visit(e.getRetVal(), other.getRetVal()))
            return false;

        if (!visit(e.getThrowVal(), other.getThrowVal()))
            return false;

        if (!visit(e.getMOPParameters(), other.getMOPParameters()))
            return false;

        if (!visit(e.getMOPParametersOnSpec(), other.getMOPParametersOnSpec()))
            return false;

        //blocks
        if (!visit(e.getAction(), other.getAction()))
            return false;

        //thread blocked vars
        if (e.getThreadBlockedVar().size() != other.getThreadBlockedVar().size())
            return false;

        for (int i = 0; i < e.getThreadBlockedVar().size(); i++) {
            if (!e.getThreadBlockedVar().get(i).equals(other.getThreadBlockedVar().get(i)))
                return false;
        }

        if (!e.getEndObjectType().getOp().equals(other.getEndObjectType().getOp()))
            return false;

        return true;
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
        if (!(arg instanceof BlockStmt))
            return false;

        if (n == arg) {
            return true;
        }

        if (n == null || arg == null)
            return false;

        BlockStmt other = (BlockStmt) arg;
        if (n.getStmts().size() != other.getStmts().size())
            return false;

        for (int i = 0; i < n.getStmts().size(); i++) {
            Statement s1 = n.getStmts().get(i);
            Statement s2 = other.getStmts().get(i);

            if (!s1.equals(s2))
                return false;
        }

        return true;
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
