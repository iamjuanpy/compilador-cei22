package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;

public interface ClassEntry {
    void correctlyDeclared() throws SemanticException;
    void consolidate() throws SemanticException;

    String getName();
    int getLine();
    HashMap<String, Attribute> getAttributeHashMap();
    HashMap<String, Method> getMethodHashMap();
    boolean isConcreteClass();

    void checkInheritanceCircularity(HashMap<String, Token> inheritanceMap) throws SemanticException;

    void addAttribute(Attribute attribute) throws SemanticException;
    void addConstructor(Constructor constructor) throws SemanticException;
    void addMethod(Method method) throws SemanticException;

    void setAncestorClass(Token classToken);
    void addMultipleInheritence(Token interfaceToken) throws SemanticException;
}
