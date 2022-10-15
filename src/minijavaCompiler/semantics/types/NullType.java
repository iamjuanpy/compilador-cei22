package minijavaCompiler.semantics.types;

public class NullType implements Type{

    public NullType(){}

    public String getTypeName() {return "null";}
    public int getLine() {return 0;}
    public boolean isPrimitive() {return false;}

    public boolean equals(Type type) {return "null".equals(getTypeName());}

    public boolean isSubtypeOf(Type type) {return !type.isPrimitive();}
}
