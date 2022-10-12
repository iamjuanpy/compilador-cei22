package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeThisAccess implements NodeAccess {

    private Token token;
    private String className;
    private NodeChaining optChaining;

    public NodeThisAccess(Token id, String className){
        this.token = id;
        this.className = className;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() {
        Type thisType = new ReferenceType(new Token(TokenType.classID,className,0));
        return null;
    }
}
