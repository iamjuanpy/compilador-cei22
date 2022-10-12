package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

import java.util.List;

public class NodeMethodChaining implements NodeChaining{

    private Token methodToken;
    public List<NodeExpression> parameterList;
    private NodeChaining optChaining;

    public NodeMethodChaining(Token id){
        this.methodToken = id;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public void setParameterList(List<NodeExpression> parameterList){
        this.parameterList = parameterList;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check(Type previousAccessType) {
        return null;
    }
}
