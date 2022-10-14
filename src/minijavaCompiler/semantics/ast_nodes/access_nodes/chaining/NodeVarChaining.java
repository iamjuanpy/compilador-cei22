package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeVarChaining implements NodeChaining {

    private Token variableToken;
    private NodeChaining optChaining;

    public NodeVarChaining(Token id){
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

    public Type check(Type previousAccessType) throws SemanticException {
        String className = previousAccessType.getTypeName();

        if (attributeExistsInClass(className) && (attributeIsPublic(className) || attributeIsDeclaredInCurrentClass(className))) {
            Type variableType = symbolTable.getClass(className).getAtrribute(variableToken.lexeme).getType();
            if (optChaining != null) {
                if (variableType.isPrimitive())
                    throw new SemanticException("No se puede encadenar a tipo primitivo", variableToken.lexeme, variableToken.lineNumber);
                return optChaining.check(variableType);
            } else return variableType;
        } else throw new SemanticException("El atributo "+variableToken.lexeme+" no existe o no es accesible", variableToken.lexeme, variableToken.lineNumber);
    }

    private boolean attributeExistsInClass(String className) {return symbolTable.getClass(className).isAttribute(variableToken.lexeme);}

    private boolean attributeIsPublic(String className) {return symbolTable.getClass(className).getAtrribute(variableToken.lexeme).isPublic();}

    private boolean attributeIsDeclaredInCurrentClass(String className) {return symbolTable.getClass(className).getAtrribute(variableToken.lexeme).getClassDeclared().equals(symbolTable.currentClass);}
}