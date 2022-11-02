package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class Parameter implements Variable {

    private Token parameterToken;
    private Type parameterType;
    private int offset;

    public Parameter(Type type, Token token) {
        parameterToken = token;
        parameterType = type;
    }

    public String getName() {
        return parameterToken.lexeme;
    }
    public int getLine() {
        return parameterToken.lineNumber;
    }
    public Type getType() {return parameterType;}

    public void correctlyDeclared() throws SemanticException {
        if (!parameterType.isPrimitive() && !symbolTable.classExists(parameterType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un parametro de tipo "+parameterType.getTypeName()+", la clase no existe", parameterType.getTypeName(), parameterType.getLine());
    }

    public boolean equals(Parameter p) { return parameterType.equals(p.getType());}

    public void setOffset(int offset) {this.offset = offset;}
    public int getOffset(){return offset;}
}
