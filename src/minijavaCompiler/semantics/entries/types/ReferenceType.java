package minijavaCompiler.semantics.entries.types;

import minijavaCompiler.lexical.Token;

public class ReferenceType implements Type{

    private Token classToken;

    public ReferenceType(Token typeId) {
        classToken = typeId;
    }

    public String getTypeName() {return classToken.lexeme;}
    public int getLine(){return classToken.lineNumber;}

    public boolean isPrimitive() {return false;}

    public boolean equals(Type type) {return classToken.lexeme.equals(type.getTypeName());}

}
