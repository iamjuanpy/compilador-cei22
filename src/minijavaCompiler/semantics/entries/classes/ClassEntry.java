package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;
import java.util.Set;

public interface ClassEntry {
    void correctlyDeclared() throws SemanticException;
    void consolidate() throws SemanticException;
    void checkSentences() throws SemanticException;

    String getName();
    int getLine();
    HashMap<String, Attribute> getAttributeHashMap();
    HashMap<String, Method> getMethodHashMap();
    boolean isConcreteClass();

    boolean isAttribute(String identifier);
    Attribute getAtrribute(String identifier);
    boolean isMethod(String identifier);
    Method getMethod(String identifier);
    Constructor getConstructor();

    void checkInheritanceCircularity(HashMap<String, Token> inheritanceMap) throws SemanticException;

    void addAttribute(Attribute attribute) throws SemanticException;
    void addConstructor(Constructor constructor) throws SemanticException;
    void addMethod(Method method) throws SemanticException;

    void setAncestorClass(Token classToken);
    void addMultipleInheritence(Token interfaceToken) throws SemanticException;

    Set<String> getInheritanceSet();

    void generateCode();
    void setOffsets();
    int getLastAttributeOffset();
    int getLastMethodOffset();

    String getVTableLabel();

}
