package minijavaCompiler.semantics.entries.types;

import minijavaCompiler.lexical.Token;

public class ReferenceType implements Type{

    private Token typeClassId;

    public ReferenceType(Token typeId) {
        typeClassId = typeId;
    }

}
