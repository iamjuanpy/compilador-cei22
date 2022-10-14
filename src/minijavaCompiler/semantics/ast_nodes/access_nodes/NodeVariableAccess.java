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

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return false;
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
        else if (symbolTable.currentClass.isAttribute(variableToken.lexeme)) {
            if (currentUnitIsConstructor() || currentUnitIsDynamicMethod())
                variable = symbolTable.currentClass.getAtrribute(variableToken.lexeme);
            else throw new SemanticException("No se puede acceder a variable de instancia "+variableToken.lexeme+" desde un metodo estático", variableToken.lexeme, variableToken.lineNumber);
        }
        else throw new SemanticException("No se encuentra variable "+ variableToken.lexeme+" en el ambiente de referenciamiento", variableToken.lexeme, variableToken.lineNumber);

        if (optChaining != null) {
            return optChaining.check(variable.getType());
        } else return variable.getType();
    }

    private boolean currentUnitIsConstructor() {return !symbolTable.currentUnit.isMethod();}
    private boolean currentUnitIsDynamicMethod() {return !((Method) symbolTable.currentUnit).isStatic();}

}
