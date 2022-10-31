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
        if (!access.isMethodCall()) // Chequea que sea llamada a const/metodo/metodo estatico o multiples encadenados con encadenado final metodo
            throw new SemanticException("Se esperaba una llamada a método estático o dinámico/constructor", semicolonToken.lexeme, semicolonToken.lineNumber);
    }

    public boolean isReturn(){return false;}
    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        access.generateCode();
    }

}
