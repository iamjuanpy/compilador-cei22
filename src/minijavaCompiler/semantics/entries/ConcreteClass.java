package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;

import java.util.HashMap;
import java.util.List;

public class ConcreteClass implements ClassEntry {

    private Token idToken;
    private HashMap<String, Method> methodHashMap;
    private HashMap<String, Attribute> attributeHashMap;
    private HashMap<String, Constructor> constructorHashMap;
    private String extendsClass;
    private List<String> implementsInts;
    private boolean consolidated;

    public ConcreteClass(Token token){
        this.idToken = token;
    }

    public void isWellDeclared() {
    }

    public void consolidate() {
        if (!consolidated){

        }
    }

    public String getName() {
        return idToken.lexeme;
    }
    public int getLine() {
        return idToken.lineNumber;
    }

    public boolean hasConstructor(Constructor constructorEntry) {
        return (constructorHashMap.get(constructorEntry.getName()) != null);
    }

    public boolean hasMethod(Method methodEntry) {
        return (methodHashMap.get(methodEntry.getName()) != null);
    }

    public boolean hasAttribute(Attribute attr) {
        return (attributeHashMap.get(attr.getName()) != null);
    }

    public void addMethod(Method method) {

    }

    public void addAttribute(Attribute attr) {

    }

    public void addConstructor(Constructor constructor) {

    }
}
