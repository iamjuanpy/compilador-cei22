package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

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

    void addExtends(Token extendClass);
    void addImplementsOrInterfaceExtends(Token implement) throws SemanticException;
}
