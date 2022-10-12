package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

public class NodeParenthesizedExp implements NodeAccess{

    private NodeExpression expression;
    private NodeChaining optChaining;

    public NodeParenthesizedExp(NodeExpression expression) {
        this.expression = expression;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }
    public boolean isMethodAccess() {
        if (optChaining != null)
            return optChaining.isMethodAccess();
        else return false;
    }

    public Type check() throws SemanticException {
        if (optChaining != null) {
            return optChaining.check(expression.check());
        } else return expression.check();
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }
}
