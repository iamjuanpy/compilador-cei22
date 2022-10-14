package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;

public class NodeCall implements NodeSentence{

    private NodeAccess access;
    private Token semicolonToken;

    public NodeCall(NodeAccess access, Token semicolonToken){
        this.access = access;
        this.semicolonToken = semicolonToken;
    }

    public void check() throws SemanticException {
        access.check();
        if (!access.isMethodCall())
            throw new SemanticException("Se esperaba una llamada a método/constructor", semicolonToken.lexeme, semicolonToken.lineNumber);
    }

    public boolean isVariableDeclaration() {
        return false;
    }
}
