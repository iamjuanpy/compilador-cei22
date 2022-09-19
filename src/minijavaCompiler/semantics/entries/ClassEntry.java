package minijavaCompiler.semantics.entries;

public interface ClassEntry {
    public void isWellDeclared();
    public void consolidate();

    String getName();
    int getLine();

    void addAttribute(Attribute attr);
    void addConstructor(Constructor constructor);
    void addMethod(Method method);

    boolean hasConstructor(Constructor constructorEntry);
    boolean hasMethod(Method methodEntry);
    boolean hasAttribute(Attribute attr);
}
