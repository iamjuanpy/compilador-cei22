package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeVarChaining implements NodeChaining {

    private Token token;
    private NodeChaining optChaining;

    public NodeVarChaining(Token id){
        this.token = id;
    }

    public void setChaining(NodeChaining chaining) {

    }

    public Type check() {return null;}

    public Type check(Type previousAccessType) {
        return null;
    }
}
