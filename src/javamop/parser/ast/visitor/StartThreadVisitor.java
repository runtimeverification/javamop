package javamop.parser.ast.visitor;

import javamop.parser.ast.*;
import javamop.parser.ast.body.*;
import javamop.parser.ast.expr.*;
import javamop.parser.ast.stmt.*;
import javamop.parser.ast.type.*;
import javamop.parser.ast.mopspec.*;
import javamop.parser.ast.aspectj.*;;

public class StartThreadVisitor implements GenericVisitor<String, Object>{

	public String visit(Node n, Object arg){
		return null;
	}

	// - JavaMOP components

	public String visit(MOPSpecFile f, Object arg){
		return null;
	}

	public String visit(JavaMOPSpec s, Object arg){
		return null;
	}

	public String visit(MOPParameter p, Object arg){
		return null;
	}

	public String visit(EventDefinition e, Object arg){
		return null;
	}

	public String visit(PropertyAndHandlers p, Object arg){
		return null;
	}

	public String visit(Formula f, Object arg){
		return null;
	}

	// - AspectJ components --------------------

	public String visit(WildcardParameter w, Object arg){
		return null;
	}

	public String visit(ArgsPointCut p, Object arg){
		return "";
	}

	public String visit(CombinedPointCut p, Object arg){
		String startThread = "";
		
		for(PointCut p2 : p.getPointcuts()){
			String temp = p2.accept(this, arg);
			if(temp != null){
				if(temp.length() != 0 && startThread.length() != 0)
					return null;

				if(temp.length() != 0)
					startThread = temp;
			} else
				return null;
		}
		
		return startThread;
	}

	public String visit(NotPointCut p, Object arg){
		return p.getPointCut().accept(this, arg);
	}

	public String visit(ConditionPointCut p, Object arg){
		return "";
	}

	public String visit(FieldPointCut p, Object arg){
		return "";
	}

	public String visit(MethodPointCut p, Object arg){
		return "";
	}

	public String visit(TargetPointCut p, Object arg){
		return "";
	}

	public String visit(ThisPointCut p, Object arg){
		return "";
	}

	public String visit(CFlowPointCut p, Object arg){
		return p.getPointCut().accept(this, arg);
	}

	public String visit(IFPointCut p, Object arg){
		return "";
	}

	public String visit(IDPointCut p, Object arg){
		return "";
	}

	public String visit(WithinPointCut p, Object arg){
		return "";
	}

	public String visit(ThreadPointCut p, Object arg){
		return "";
	}
	
	public String visit(ThreadNamePointCut p, Object arg){
		return "";
	}
	
	public String visit(EndProgramPointCut p, Object arg){
		return "";
	}

	public String visit(EndThreadPointCut p, Object arg){
		return "";
	}
	
	public String visit(EndObjectPointCut p, Object arg){
		return "";
	}

	public String visit(StartThreadPointCut p, Object arg){
		return "exist";
	}

	public String visit(FieldPattern p, Object arg){
		return null;
	}

	public String visit(MethodPattern p, Object arg){
		return null;
	}

	public String visit(CombinedTypePattern p, Object arg){
		return null;
	}

	public String visit(NotTypePattern p, Object arg){
		return null;
	}

	public String visit(BaseTypePattern p, Object arg){
		return null;
	}

	// - Compilation Unit ----------------------------------

	public String visit(CompilationUnit n, Object arg){
		return null;
	}

	public String visit(PackageDeclaration n, Object arg){
		return null;
	}

	public String visit(ImportDeclaration n, Object arg){
		return null;
	}

	public String visit(TypeParameter n, Object arg){
		return null;
	}

	// - Body ----------------------------------------------

	public String visit(ClassOrInterfaceDeclaration n, Object arg){
		return null;
	}

	public String visit(EnumDeclaration n, Object arg){
		return null;
	}

	public String visit(EmptyTypeDeclaration n, Object arg){
		return null;
	}

	public String visit(EnumConstantDeclaration n, Object arg){
		return null;
	}

	public String visit(AnnotationDeclaration n, Object arg){
		return null;
	}

	public String visit(AnnotationMemberDeclaration n, Object arg){
		return null;
	}

	public String visit(FieldDeclaration n, Object arg){
		return null;
	}

	public String visit(VariableDeclarator n, Object arg){
		return null;
	}

	public String visit(VariableDeclaratorId n, Object arg){
		return null;
	}

	public String visit(ConstructorDeclaration n, Object arg){
		return null;
	}

	public String visit(MethodDeclaration n, Object arg){
		return null;
	}

	public String visit(Parameter n, Object arg){
		return null;
	}

	public String visit(EmptyMemberDeclaration n, Object arg){
		return null;
	}

	public String visit(InitializerDeclaration n, Object arg){
		return null;
	}

	// - Type ----------------------------------------------

	public String visit(ClassOrInterfaceType n, Object arg){
		return null;
	}

	public String visit(PrimitiveType n, Object arg){
		return null;
	}

	public String visit(ReferenceType n, Object arg){
		return null;
	}

	public String visit(VoidType n, Object arg){
		return null;
	}

	public String visit(WildcardType n, Object arg){
		return null;
	}

	// - Expression ----------------------------------------

	public String visit(ArrayAccessExpr n, Object arg){
		return null;
	}

	public String visit(ArrayCreationExpr n, Object arg){
		return null;
	}

	public String visit(ArrayInitializerExpr n, Object arg){
		return null;
	}

	public String visit(AssignExpr n, Object arg){
		return null;
	}

	public String visit(BinaryExpr n, Object arg){
		return null;
	}

	public String visit(CastExpr n, Object arg){
		return null;
	}

	public String visit(ClassExpr n, Object arg){
		return null;
	}

	public String visit(ConditionalExpr n, Object arg){
		return null;
	}

	public String visit(EnclosedExpr n, Object arg){
		return null;
	}

	public String visit(FieldAccessExpr n, Object arg){
		return null;
	}

	public String visit(InstanceOfExpr n, Object arg){
		return null;
	}

	public String visit(StringLiteralExpr n, Object arg){
		return null;
	}

	public String visit(IntegerLiteralExpr n, Object arg){
		return null;
	}

	public String visit(LongLiteralExpr n, Object arg){
		return null;
	}

	public String visit(IntegerLiteralMinValueExpr n, Object arg){
		return null;
	}

	public String visit(LongLiteralMinValueExpr n, Object arg){
		return null;
	}

	public String visit(CharLiteralExpr n, Object arg){
		return null;
	}

	public String visit(DoubleLiteralExpr n, Object arg){
		return null;
	}

	public String visit(BooleanLiteralExpr n, Object arg){
		return null;
	}

	public String visit(NullLiteralExpr n, Object arg){
		return null;
	}

	public String visit(MethodCallExpr n, Object arg){
		return null;
	}

	public String visit(NameExpr n, Object arg){
		return null;
	}

	public String visit(ObjectCreationExpr n, Object arg){
		return null;
	}

	public String visit(QualifiedNameExpr n, Object arg){
		return null;
	}

	public String visit(SuperMemberAccessExpr n, Object arg){
		return null;
	}

	public String visit(ThisExpr n, Object arg){
		return null;
	}

	public String visit(SuperExpr n, Object arg){
		return null;
	}

	public String visit(UnaryExpr n, Object arg){
		return null;
	}

	public String visit(VariableDeclarationExpr n, Object arg){
		return null;
	}

	public String visit(MarkerAnnotationExpr n, Object arg){
		return null;
	}

	public String visit(SingleMemberAnnotationExpr n, Object arg){
		return null;
	}

	public String visit(NormalAnnotationExpr n, Object arg){
		return null;
	}

	public String visit(MemberValuePair n, Object arg){
		return null;
	}

	// - Statements ----------------------------------------

	public String visit(ExplicitConstructorInvocationStmt n, Object arg){
		return null;
	}

	public String visit(TypeDeclarationStmt n, Object arg){
		return null;
	}

	public String visit(AssertStmt n, Object arg){
		return null;
	}

	public String visit(BlockStmt n, Object arg){
		return null;
	}

	public String visit(LabeledStmt n, Object arg){
		return null;
	}

	public String visit(EmptyStmt n, Object arg){
		return null;
	}

	public String visit(ExpressionStmt n, Object arg){
		return null;
	}

	public String visit(SwitchStmt n, Object arg){
		return null;
	}

	public String visit(SwitchEntryStmt n, Object arg){
		return null;
	}

	public String visit(BreakStmt n, Object arg){
		return null;
	}

	public String visit(ReturnStmt n, Object arg){
		return null;
	}

	public String visit(IfStmt n, Object arg){
		return null;
	}

	public String visit(WhileStmt n, Object arg){
		return null;
	}

	public String visit(ContinueStmt n, Object arg){
		return null;
	}

	public String visit(DoStmt n, Object arg){
		return null;
	}

	public String visit(ForeachStmt n, Object arg){
		return null;
	}

	public String visit(ForStmt n, Object arg){
		return null;
	}

	public String visit(ThrowStmt n, Object arg){
		return null;
	}

	public String visit(SynchronizedStmt n, Object arg){
		return null;
	}

	public String visit(TryStmt n, Object arg){
		return null;
	}

	public String visit(CatchClause n, Object arg){
		return null;
	}

}
