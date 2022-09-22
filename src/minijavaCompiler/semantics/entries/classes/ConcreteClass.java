package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.classID;

public class ConcreteClass implements ClassEntry {

    private Token classIDToken;
    private HashMap<String, Attribute> attributeHashMap;
    private Constructor constructor;
    private boolean hasConstructor;
    private HashMap<String, Method> methodHashMap;
    private Token ancestorClassToken;
    private HashMap<String,Token> implementsInts;
    private boolean consolidated;

    public ConcreteClass(Token token){
        this.classIDToken = token;
        attributeHashMap = new HashMap<>();
        methodHashMap = new HashMap<>();
        implementsInts = new HashMap<>();
        ancestorClassToken = new Token(classID, "Object", token.lineNumber); // default: idClase extends Object {}
        createDefaultConstructor();
    }

    private void createDefaultConstructor() {
        constructor = new Constructor(classIDToken);
        hasConstructor = false;
    }

    public String getName() {return classIDToken.lexeme;}
    public int getLine() {return classIDToken.lineNumber;}
    public boolean isConcreteClass(){return true;}

    public void hasCircularInheritence(HashMap<String, Token> inheritance) throws SemanticException {
        System.out.println(inheritance);
        // Si llegue a object no hay herencia circular
        if (classIDToken.lexeme.equals("Object"))
            return;
        // Si no estoy en object, reviso si ya tengo en la lista recorrida la misma clase
        System.out.println("A poner "+classIDToken.toString()+" -> "+ancestorClassToken.toString());
        if (inheritance.get(ancestorClassToken.lexeme) == null) {
            // Si no la tengo, sigo
            inheritance.put(classIDToken.lexeme, ancestorClassToken);
            symbolTable.getClass(ancestorClassToken.lexeme).hasCircularInheritence(inheritance);
        } else {
            // Si la tengo, reporto el error con la linea mas lejana (COMO)
            throw new SemanticException("No puede haber herencia circular", ancestorClassToken.lexeme, ancestorClassToken.lineNumber);
        }
    }

    public void isWellDeclared() throws SemanticException {
        System.out.println("chequeando "+classIDToken.lexeme);
        checkExtends();
        checkImplements();
        checkAttributes();
        checkMethods();
        checkConstructor();
    }

    public void consolidate() {
        if (!consolidated){
            consolidateAncestors();

            consolidated = true;
        }
    }

    private void consolidateAncestors() {
        symbolTable.getClass(ancestorClassToken.lexeme).consolidate();
        for (Token interfaceID : implementsInts.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }

    private void checkExtends() throws SemanticException {
        if (ancestorClassToken != null && !symbolTable.classExists(ancestorClassToken.lexeme))
            throw new SemanticException("No se puede extender a la clase "+ ancestorClassToken.lexeme+", no existe", ancestorClassToken.lexeme, ancestorClassToken.lineNumber);
        HashMap<String, Token> inheritanceList = new HashMap<>();
        inheritanceList.put(classIDToken.lexeme, ancestorClassToken);
        hasCircularInheritence(inheritanceList);
    }

    private void checkImplements() throws SemanticException {
        for (Token impInterface : implementsInts.values()) {
            if ((impInterface.lexeme).equals(classIDToken.lexeme))
                throw new SemanticException("La clase "+ ancestorClassToken.lexeme+" no se puede implementar como interface a si misma", ancestorClassToken.lexeme, ancestorClassToken.lineNumber);
            if (!symbolTable.classExists(impInterface.lexeme))
                throw new SemanticException("No se puede implementar la interface "+impInterface.lexeme+", no existe", impInterface.lexeme, impInterface.lineNumber);
            else if (symbolTable.getClass(impInterface.lexeme).isConcreteClass())
                throw new SemanticException("No se puede implementar "+impInterface.lexeme+", es una clase concreta", impInterface.lexeme, impInterface.lineNumber);
        }
    }

    private void checkAttributes() throws SemanticException {
        for (Attribute attribute : attributeHashMap.values())
            attribute.isWellDeclared();
    }

    private void checkConstructor() throws SemanticException {
        constructor.isWellDeclared();
    }

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.isWellDeclared();
    }

    public void setAncestorClass(Token ancestorClass){
        this.ancestorClassToken = ancestorClass;}

    public void addImplementsOrInterfaceExtends(Token implement) throws SemanticException {
        if (implementsInts.get(implement.lexeme) != null) {
            throw new SemanticException("No se puede implementar dos veces la interfaz "+implement.lexeme,implement.lexeme, implement.lineNumber);
        } else implementsInts.put(implement.lexeme, implement);
    }

    public void addMethod(Method method) throws SemanticException {
        if (alreadyHasMethodWithName(method.getName())) {
            throw new SemanticException("Hay dos miembros llamados "+method.getName(), method.getName(), method.getLine());
        } else methodHashMap.put(method.getName(), method);
    }

    private boolean alreadyHasMethodWithName(String name) {return methodHashMap.get(name) != null;}

    public void addAttribute(Attribute attribute) throws SemanticException {
        if (alreadyHasAttributeWithName(attribute.getName())) {
            throw new SemanticException("Hay dos miembros llamados "+attribute.getName(), attribute.getName(), attribute.getLine());
        } else attributeHashMap.put(attribute.getName(), attribute);
    }

    private boolean alreadyHasAttributeWithName(String name) {return attributeHashMap.get(name) != null;}

    public void addConstructor(Constructor constructor) throws SemanticException {
        if (hasConstructor) {
            throw new SemanticException("No se puede declarar mas de un constructor",constructor.getName(), constructor.getLine());
        } else {
            this.constructor = constructor;
            hasConstructor = true;
        }
    }
}
