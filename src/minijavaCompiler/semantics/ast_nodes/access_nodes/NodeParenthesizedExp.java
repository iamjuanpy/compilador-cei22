package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

public class NodeParenthesizedExp implements NodeAccess{

    private NodeExpression expression;

    public NodeParenthesizedExp(NodeExpression expression) {
        this.expression = expression;
    }

    public Type check() {
        return null;
    }

    public void setChaining(NodeChaining chaining) {

    }
}
