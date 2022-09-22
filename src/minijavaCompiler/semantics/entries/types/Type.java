package minijavaCompiler.semantics.entries.types;

public interface Type {

    String getTypeName();
    int getLine();
    boolean isPrimitive();

    boolean equals(Type type);
}
