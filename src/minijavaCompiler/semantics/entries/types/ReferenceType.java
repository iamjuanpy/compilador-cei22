package minijavaCompiler.semantics.entries.types;

import minijavaCompiler.lexical.Token;

public class ReferenceType implements Type{

    private Token typeClassId;

    public ReferenceType(Token typeId) {
        typeClassId = typeId;
    }

    public String getTypeName() {return typeClassId.lexeme;}
    public int getLine(){return typeClassId.lineNumber;}

    public boolean isPrimitive() {return false;}

    public boolean equals(Type type) {return typeClassId.lexeme.equals(type.getTypeName());}

}
