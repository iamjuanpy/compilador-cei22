package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

import java.util.List;

public class NodeConstructorCall {

    private Token token;
    private List<NodeExpression> parameterList;
    private NodeChaining optChaining;

}
