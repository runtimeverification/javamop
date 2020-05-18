package javamop.parser.astex.mopspec;

import javamop.parser.ast.mopspec.MOPParameter;
import javamop.parser.ast.mopspec.MOPParameters;
import javamop.parser.ast.stmt.BlockStmt;
import javamop.parser.astex.ExtNode;
import javamop.parser.astex.visitor.GenericVisitor;
import javamop.parser.astex.visitor.VoidVisitor;

import java.util.List;

public class InternalEventExt extends ExtNode {
    private final String name;
    private final MOPParameters parameters;
    private final BlockStmt block;

    public InternalEventExt(int line, int column, String name, List<MOPParameter> parameters, BlockStmt block) {
        super(line, column);
        this.name = name;
        this.parameters = new MOPParameters(parameters);
        this.block = block;
    }

    public InternalEventExt(int line, int column, InternalEventExt ie) {
        super(line, column);
        this.name = ie.getName();
        this.parameters = ie.getParameters();
        this.block = ie.getBlock();
    }

    public String getName() { return this.name; }

    public MOPParameters getParameters() {
        return this.parameters;
    }

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
