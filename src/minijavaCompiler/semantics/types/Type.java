package minijavaCompiler.semantics.types;

public interface Type {

    String getTypeName();
    int getLine();
    boolean isPrimitive();

    boolean equals(Type type);

    boolean isSubtypeOf(Type type);
}
