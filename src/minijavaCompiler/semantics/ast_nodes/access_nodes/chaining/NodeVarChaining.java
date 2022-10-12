package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeVarChaining implements NodeChaining {

    private Token token;
    private NodeChaining optChaining;

    public NodeVarChaining(Token id){
        this.token = id;
    }

    public boolean isVariableAccess() {
        if (optChaining == null)
            return true;
        else return optChaining.isVariableAccess();
    }

    public boolean isMethodAccess() {
        if (optChaining != null)
            return optChaining.isMethodAccess();
        else return false;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check(Type previousAccessType) {
        return null;
    }
}
