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

    public void isWellDeclared() {
    }

    public void consolidate() {
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }

    public void addMethod(Method method) {

    }

    public void addAttribute(Attribute attr) {

    }

    public void addConstructor(Constructor constructor) {

    }
}
