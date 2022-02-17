package javamop.parser.ast.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.*;
import javamop.parser.ast.aspectj.*;

/**
 * @author Julio Vilmar Gesser
 */
public interface MOPVoidVisitor<A> extends VoidVisitor<A> {

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
}
