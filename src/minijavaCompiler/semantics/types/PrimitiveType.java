package minijavaCompiler.semantics.types;

public class PrimitiveType implements Type{

    private String primitiveTypeName;

    public PrimitiveType(String typeIdentifier) {
        primitiveTypeName = typeIdentifier;
    }

    public String getTypeName() {return primitiveTypeName;}

    public int getLine() {return 0;} // NO LLEGA

    public boolean isPrimitive() {return true;}

    public boolean equals(Type type) {return primitiveTypeName.equals(type.getTypeName());}

    public boolean isSubtypeOf(Type type) {return primitiveTypeName.equals(type.getTypeName());}

}
