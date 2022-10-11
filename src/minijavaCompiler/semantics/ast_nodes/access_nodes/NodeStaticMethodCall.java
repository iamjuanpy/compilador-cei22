package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

import java.util.List;

public class NodeStaticMethodCall implements NodeAccess{

    private Token classToken;
    private Token methodToken;
    private List<NodeExpression> parameterList;
    private NodeChaining optChaining;

    public NodeStaticMethodCall(Token classID, Token methodID){
        this.classToken = classID;
        this.methodToken = methodID;
    }

    public void setParameterList(List<NodeExpression> parameterList){
        this.parameterList = parameterList;
    }


    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() {
        return null;
    }
}
