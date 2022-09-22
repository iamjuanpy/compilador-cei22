package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class Parameter {

    private Token idToken;
    private Type parameterType;

    public Parameter(Type tipoArg, Token idArg) {
        parameterType = tipoArg;
        idToken = idArg;
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }

    private Type getParameterType() {return parameterType;}

    public boolean equals(Parameter p) { return parameterType.equals(p.getParameterType());}

    public void isWellDeclared() throws SemanticException {
        if (!parameterType.isPrimitive() && !symbolTable.classExists(parameterType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un parametro de tipo "+parameterType.getTypeName()+", la clase no existe", parameterType.getTypeName(), parameterType.getLine());
    }

}
