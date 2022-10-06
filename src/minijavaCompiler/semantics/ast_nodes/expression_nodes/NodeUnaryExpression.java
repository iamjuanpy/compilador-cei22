package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeUnaryExpression implements NodeExpression {

    public NodeUnaryExpression(Token operator, NodeExpression expression){

    }

    public NodeUnaryExpression(NodeExpression expression){

    }

    public Type check() {
        return null;
    }
}
