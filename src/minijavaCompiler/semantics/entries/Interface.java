package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;

import java.util.HashMap;
import java.util.List;

public class Interface implements ClassEntry {

    private Token idToken;
    private HashMap<String, Method> methodHashMap;
    private List<String> extendsInts;
    private boolean consolidated;

    public Interface(Token token){
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

    public boolean hasAttribute(Attribute attr) {return false;} // NO LLEGA
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
