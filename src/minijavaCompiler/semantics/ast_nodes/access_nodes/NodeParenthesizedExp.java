package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.types.Type;

public class NodeParenthesizedExp implements NodeAccess{


    public Type check() {
        return null;
    }

    public void setChaining(NodeChaining chaining) {

    }
}
