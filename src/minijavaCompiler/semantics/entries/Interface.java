package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;

import java.util.HashMap;
import java.util.List;

public class Interface implements ClassEntry {

    private Token idToken;
    private HashMap<String, Method> methodHashMap;
    private List<String> extendsInts;

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

    public void addAttribute(Attribute attr) {} // NO LLEGA
    public void addConstructor(Constructor constructor) {} // NO LLEGA
}
