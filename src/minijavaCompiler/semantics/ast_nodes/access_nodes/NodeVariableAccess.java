package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Variable;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeVariableAccess implements NodeAccess {

    private Token variableToken;
    private NodeChaining optChaining;

    public NodeVariableAccess(Token id){
        this.variableToken = id;
    }

    public boolean isVariableAccess() {
        if (optChaining == null)
            return true;
        else return optChaining.isVariableAccess();
    }

    public void isMethodCall() throws SemanticException {
        if (optChaining != null)
            optChaining.isMethodCall();
        else throw new SemanticException("Se esperaba una llamada a método", variableToken.lexeme, variableToken.lineNumber);
    }
    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        Variable variable;
        if (symbolTable.currentUnit.isParameter(variableToken.lexeme))
            variable = symbolTable.currentUnit.getParameter(variableToken.lexeme);
        else if (symbolTable.currentBlock.isLocalVariable(variableToken.lexeme))
            variable = symbolTable.currentBlock.getLocalVariable(variableToken.lexeme);
        else if (symbolTable.currentClass.isAttribute(variableToken.lexeme) && isMethodOrConstructor())
            variable = symbolTable.currentClass.getAtrribute(variableToken.lexeme);
        else throw new SemanticException("No se encuentra variable "+ variableToken.lexeme+" en el ambiente de referenciamiento", variableToken.lexeme, variableToken.lineNumber);

        if (optChaining == null) {
            if (variable.getType().isPrimitive())
                throw new SemanticException("No se puede encadenar a tipo primitivo", variableToken.lexeme, variableToken.lineNumber);
            return variable.getType();
        } else return optChaining.check(variable.getType());
    }

    private boolean isMethodOrConstructor() {
        return (!symbolTable.currentUnit.isMethod() || !((Method) symbolTable.currentUnit).isStatic()); // Constructor o metodo no estatico puede usar var de instancia
    }
}
