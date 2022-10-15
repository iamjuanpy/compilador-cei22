package minijavaCompiler.semantics.types;

import minijavaCompiler.lexical.Token;

public class NullType implements Type{

    private Token nullToken;

    public NullType(Token token){
        this.nullToken = token;
    }

    public String getTypeName() {return nullToken.lexeme;}
    public int getLine() {return nullToken.lineNumber;}
    public boolean isPrimitive() {return false;}

    public boolean equals(Type type) {return nullToken.lexeme.equals(getTypeName());}

    public boolean isSubtypeOf(Type type) {return !type.isPrimitive();}
}
