package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

import java.util.List;

public class NodeMethodChaining implements NodeChaining{

    private Token methodToken;
    private List<NodeExpression> parameterList;
    private NodeChaining optChaining;

}
