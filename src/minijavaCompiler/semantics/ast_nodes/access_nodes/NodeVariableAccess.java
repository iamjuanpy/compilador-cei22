package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.types.Type;

public class NodeVariableAccess implements NodeAccess {

    private Token token;
    private NodeChaining optChaining;

    public NodeVariableAccess(Token id){
        this.token = id;
    }

    public void setChaining(NodeChaining chaining) {

    }

    public Type check() {
        return null;
    }
}
