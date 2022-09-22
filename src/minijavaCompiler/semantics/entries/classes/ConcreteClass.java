package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.SymbolTable;
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
    public HashMap<String, Attribute> getAttributeHashMap(){return attributeHashMap;}
    public HashMap<String, Method> getMethodHashMap() {return  methodHashMap;}

    public void hasCircularInheritence(HashMap<String, Token> inheritance) throws SemanticException {
        System.out.println(inheritance);
        if (classIDToken.lexeme.equals("Object")) // Si llegue a object no hay herencia circular
            return;
        System.out.println("A poner "+classIDToken.toString()+" -> "+ancestorClassToken.toString());
        if (inheritance.get(ancestorClassToken.lexeme) == null) { // Si no estoy en object, reviso si ya tengo en la lista recorrida la misma clase
            inheritance.put(classIDToken.lexeme, ancestorClassToken);
            symbolTable.getClass(ancestorClassToken.lexeme).hasCircularInheritence(inheritance);
        } else { // Si la tengo, reporto el error con la linea mas lejana
            Token lastToken = getLastInheritanceDeclaration(inheritance, ancestorClassToken);
            throw new SemanticException("No puede haber herencia circular", lastToken.lexeme, lastToken.lineNumber);
        }
    }

    private Token getLastInheritanceDeclaration(HashMap<String, Token> inheritanceList, Token lastAncestor) {
        Token lastToken = null;
        for (Token extendsClass : inheritanceList.values()) {
            if (lastToken == null || extendsClass.lineNumber >= lastToken.lineNumber)
                lastToken = extendsClass;
        }

        if (lastAncestor.lineNumber >= lastToken.lineNumber)
            return lastAncestor;
        else return lastToken;
    }

    public void correctlyDeclared() throws SemanticException {
        System.out.println("chequeando "+classIDToken.lexeme);
        checkInheritance();
        checkInterfaces();
        checkAttributes();
        checkMethods();
        checkConstructor();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            consolidateAncestors();
            checkInheritedMethods();
            checkInheritedAttributes();
            consolidated = true;
        }
    }

    private void consolidateAncestors() throws SemanticException {
        symbolTable.getClass(ancestorClassToken.lexeme).consolidate();
        for (Token interfaceID : implementsInts.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }

    private void checkInheritedAttributes() throws SemanticException {
        for (Attribute attr : symbolTable.getClass(ancestorClassToken.lexeme).getAttributeHashMap().values()){
            if (attributeHashMap.get(attr.getName()) == null) {
                if (attr.isPublic())
                    attributeHashMap.put(attr.getName(), attr);
            } else throw new SemanticException("No se puede declarar un atributo con el mismo nombre que un atributo de clase padre", attr.getName(), attributeHashMap.get(attr.getName()).getLine());
        }
    }

    private void checkInheritedMethods() throws SemanticException {
        checkRedefinedMethods();
        checkImplementedMethods();
    }

    private void checkRedefinedMethods() throws SemanticException {
        for (Method method : symbolTable.getClass(ancestorClassToken.lexeme).getMethodHashMap().values()){
            if (methodHashMap.get(method.getName()) == null) {  // Si no redefine el metodo, se agrega
                methodHashMap.put(method.getName(), method);
            } else if (!method.equals(methodHashMap.get(method.getName()))) {   // Si se redefine pero con distintos parametros o tipo de retorno, error semantico
                throw new SemanticException("No se puede redefinir un metodo con el mismo nombre", method.getName(), methodHashMap.get(method.getName()).getLine());
            }
        }
    }

    private void checkImplementedMethods() throws SemanticException {
        for (Token intface : implementsInts.values()){
            for (Method method : symbolTable.getClass(intface.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) == null) {          // Si no implementa un metodo, error
                    throw new SemanticException("Falta implementar el metodo "+method.getName(), method.getName(), method.getLine());
                } else if (!method.equals(methodHashMap.get(method.getName()))) {   // Si lo implementa pero con distintos parametros, error
                    throw new SemanticException("El metodo "+method.getName()+" está mal implementado", method.getName(), methodHashMap.get(method.getName()).getLine());
                }
            }
        }
    }

    private void checkInheritance() throws SemanticException {
        if (ancestorClassToken != null && !symbolTable.classExists(ancestorClassToken.lexeme))
            throw new SemanticException("No se puede extender a la clase "+ ancestorClassToken.lexeme+", no existe", ancestorClassToken.lexeme, ancestorClassToken.lineNumber);
        hasCircularInheritence(new HashMap<>());
    }

    private void checkInterfaces() throws SemanticException {
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
            attribute.correctlyDeclared();
    }

    private void checkConstructor() throws SemanticException {
        constructor.correctlyDeclared();
    }

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
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
            throw new SemanticException("Hay dos métodos llamados "+method.getName(), method.getName(), method.getLine());
        } else {
            methodHashMap.put(method.getName(), method);
            checkForMainMethod(method);
        }
    }

    private void checkForMainMethod(Method method) {
        if (symbolTable.mainMethod == null && method.isMain()) {
            symbolTable.mainMethod = method;
        }
    }

    private boolean alreadyHasMethodWithName(String name) {return methodHashMap.get(name) != null;}

    public void addAttribute(Attribute attribute) throws SemanticException {
        if (alreadyHasAttributeWithName(attribute.getName())) {
            throw new SemanticException("Hay dos atributos llamados "+attribute.getName(), attribute.getName(), attribute.getLine());
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
