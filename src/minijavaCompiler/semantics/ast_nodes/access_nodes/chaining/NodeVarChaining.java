package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;

public class NodeVarChaining implements NodeChaining {

    private Token token;
    private NodeChaining optChaining;

    public NodeVarChaining(Token id){
        this.token = id;
    }

    public void setChaining(NodeChaining chaining) {

    }
}
