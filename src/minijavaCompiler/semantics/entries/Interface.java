package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

import java.util.HashMap;
import java.util.List;

public class Interface implements ClassEntry {

    private Token idToken;
    private HashMap<String, Method> methodHashMap;
    private HashMap<String, Token> extendsInts;
    private boolean consolidated;

    public Interface(Token token){
        this.idToken = token;
        methodHashMap = new HashMap<>();
        extendsInts = new HashMap<>();
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

    public boolean hasAttribute(Attribute attr) {return false;} // NO LLEGA

    public void addExtends(Token extendClass) {} // NO LLEGA

    public void addImplementsOrInterfaceExtends(Token implement) throws SemanticException {
        if (extendsInts.get(implement.lexeme) == null)
            extendsInts.put(implement.lexeme, implement);
        else throw new SemanticException(implement.lexeme, implement.lineNumber);
    }

    public boolean hasConstructor(Constructor constructorEntry) {return false;} // NO LLEGA

    public boolean hasMethod(Method methodEntry) {
        return (methodHashMap.get(methodEntry.getName()) != null);
    }

    public void addAttribute(Attribute attr) {} // NO LLEGA
    public void addConstructor(Constructor constructor) {} // NO LLEGA

    public void addMethod(Method method) {
        methodHashMap.put(method.getName(), method);
    }

}
