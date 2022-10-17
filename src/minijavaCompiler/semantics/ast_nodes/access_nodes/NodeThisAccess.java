package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.entries.Method;
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

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return false;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        Type thisType = new ReferenceType(new Token(TokenType.classID,className,0));

        checkNotCalledInStaticMethod();

        if (optChaining == null) {
            return thisType;
        } else return optChaining.check(thisType);
    }

    private void checkNotCalledInStaticMethod() throws SemanticException {
        if (symbolTable.currentUnit.isMethod())
            if (((Method) symbolTable.currentUnit).isStatic())
                throw new SemanticException("No se puede tener accesos this en metodo estático", token.lexeme, token.lineNumber);
    }
}
