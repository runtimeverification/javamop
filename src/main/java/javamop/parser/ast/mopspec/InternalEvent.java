package javamop.parser.ast.mopspec;

import javamop.parser.ast.Node;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.ast.visitor.GenericVisitor;
import javamop.parser.ast.visitor.VoidVisitor;

import java.util.List;

public class InternalEvent extends Node {
    private final String name;
    private final MOPParameters parameters;
    private final BlockStmt block;

    public InternalEvent(int line, int column, String name,List<MOPParameter> parameters, BlockStmt block) {
        super(line, column);
        this.name = name;
        this.parameters = new MOPParameters(parameters);
        this.block = block;
    }

    public String getName() { return this.name; }

    public MOPParameters getParameters() { return this.parameters; }

    public BlockStmt getBlock() {
        return this.block;
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }
}
