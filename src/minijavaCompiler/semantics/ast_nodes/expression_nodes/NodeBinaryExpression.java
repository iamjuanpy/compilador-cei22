package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeBinaryExpression implements NodeExpression {

    public NodeBinaryExpression(Token operator, NodeExpression leftSide, NodeExpression rightSide){

    }

    public Type check() {
        return null;
    }
}
