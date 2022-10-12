package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeLocalVariable;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Variable;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeVariableAccess implements NodeAccess {

    private Token token;
    private NodeChaining optChaining;

    public NodeVariableAccess(Token id){
        this.token = id;
    }

    public boolean isVariableAccess() {
        if (optChaining == null)
            return true;
        else return optChaining.isVariableAccess();
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        Variable variable;
        if (symbolTable.currentUnit.isParameter(token.lexeme))
            variable = symbolTable.currentUnit.getParameter(token.lexeme);
        else if (symbolTable.currentBlock.isLocalVariable(token.lexeme))
            variable = symbolTable.currentBlock.getLocalVariable(token.lexeme);
        else if (symbolTable.currentClass.isAttribute(token.lexeme) && isMethodOrConstructor())
            variable = symbolTable.currentClass.getAtrribute(token.lexeme);
        else throw new SemanticException("No se encuentra variable "+token.lexeme+" en el ambiente de referenciamiento",token.lexeme, token.lineNumber);

        if (optChaining == null)
            return variable.getType();
        else return optChaining.check(variable.getType());
    }

    private boolean isMethodOrConstructor() {
        return (!symbolTable.currentUnit.isMethod() || !((Method) symbolTable.currentUnit).isStatic()); // Constructor o metodo no estatico puede usar var de instancia
    }
}
