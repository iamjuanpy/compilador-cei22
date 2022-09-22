package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;

import static minijavaCompiler.Main.symbolTable;

public class Interface implements ClassEntry {

    private Token classIDToken;
    private HashMap<String, Method> methodHashMap;
    private HashMap<String, Token> extendsInts;
    private boolean consolidated;

    public Interface(Token token){
        this.classIDToken = token;
        methodHashMap = new HashMap<>();
        extendsInts = new HashMap<>();
    }

    public String getName() {
        return classIDToken.lexeme;
    }
    public int getLine() {
        return classIDToken.lineNumber;
    }
    public HashMap<String, Attribute> getAttributeHashMap() {return null;}   // No llega
    public HashMap<String, Method> getMethodHashMap() {return  methodHashMap;}
    public boolean isConcreteClass() { return false;}

    public void hasCircularInheritence(HashMap<String, Token> inheritance) throws SemanticException {

    }

    public void correctlyDeclared() throws SemanticException {
        checkInheritance();
        checkMethods();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            consolidateAncestors();
            // ver que hacer aca
            consolidated = true;
        }
    }

    private void consolidateAncestors() throws SemanticException {
        for (Token interfaceID : extendsInts.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }


    private void checkInheritance() throws SemanticException {
        for (Token extInterface : extendsInts.values()) {
            if (!symbolTable.classExists(extInterface.lexeme))
                throw new SemanticException("No se puede extender la interface "+extInterface.lexeme+", no existe", extInterface.lexeme, extInterface.lineNumber);
            else if (symbolTable.getClass(extInterface.lexeme).isConcreteClass())
                throw new SemanticException("No se puede extender "+extInterface.lexeme+", es una clase concreta", extInterface.lexeme, extInterface.lineNumber);
        }
    }

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
    }

    public void setAncestorClass(Token extendClass) {} // NO LLEGA

    public void addImplementsOrInterfaceExtends(Token implement) throws SemanticException {
        if (extendsInts.get(implement.lexeme) == null)
            extendsInts.put(implement.lexeme, implement);
        else throw new SemanticException(implement.lexeme, implement.lineNumber);
    }

    public void addAttribute(Attribute attribute) throws SemanticException {} // NO LLEGA
    public void addConstructor(Constructor constructor) throws SemanticException {} // NO LLEGA
    public void addMethod(Method method) throws SemanticException {
        if (method.isStatic())
            throw new SemanticException("Una interfaz no puede tener métodos estáticos", method.getName(), method.getLine());
        if (methodHashMap.get(method.getName()) == null)
            methodHashMap.put(method.getName(), method);
        else throw new SemanticException("Hay dos metodos llamados "+method.getName(), method.getName(), method.getLine());
    }

}
