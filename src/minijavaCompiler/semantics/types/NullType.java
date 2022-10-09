package minijavaCompiler.semantics.types;

import minijavaCompiler.lexical.Token;

public class NullType implements Type{

    private Token token;

    public NullType(Token token){
        this.token = token;
    }

    public String getTypeName() {return token.lexeme;}
    public int getLine() {return token.lineNumber;}
    public boolean isPrimitive() {return false;}

    public boolean equals(Type type) {return false;} // no llega?

    public boolean isSubtypeOf(Type type) {return !type.isPrimitive();}
}
