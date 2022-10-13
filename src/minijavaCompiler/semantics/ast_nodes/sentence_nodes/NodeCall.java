package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;

public class NodeCall implements NodeSentence{

    private NodeAccess access;

    public NodeCall(NodeAccess access){
        this.access = access;
    }

    public void check() throws SemanticException {
        access.check();
        access.isMethodCall(); // Throwea excepcion con la ultima expresion de la sentencia si no es llamada a metodo.
    }

    public boolean isVariableDeclaration() {
        return false;
    }
}
