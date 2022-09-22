package minijavaCompiler.semantics.entries.types;

public class PrimitiveType implements Type{

    private String primitiveTypeId;

    public PrimitiveType(String typeId) {
        primitiveTypeId = typeId;
    }

    public String getTypeName() {return primitiveTypeId;}

    public int getLine() {return 0;} // NO LLEGA

    public boolean isPrimitive() {return true;}

    public boolean equals(Type type) {return primitiveTypeId.equals(getTypeName());}

}
