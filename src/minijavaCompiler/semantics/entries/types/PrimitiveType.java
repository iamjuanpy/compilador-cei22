package minijavaCompiler.semantics.entries.types;

public class PrimitiveType implements Type{

    // null, void, int, char, boolean
    private String primitiveTypeId;

    public PrimitiveType(String typeId) {
        primitiveTypeId = typeId;
    }

}
