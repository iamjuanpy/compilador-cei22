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

    private Token classToken;
    private HashMap<String, Attribute> attributeHashMap;
    private Constructor constructor;
    private boolean hasUserDeclaredConstructor;
    private HashMap<String, Method> methodHashMap;
    private Token extendsClassToken;
    private HashMap<String,Token> interfacesHashMap;
    private boolean consolidated;

    public ConcreteClass(Token token){
        this.classToken = token;
        attributeHashMap = new HashMap<>();
        methodHashMap = new HashMap<>();
        interfacesHashMap = new HashMap<>();
        extendsClassToken = new Token(classID, "Object", token.lineNumber); // default: idClase extends Object {}
        createDefaultConstructor();
    }

    private void createDefaultConstructor() {
        constructor = new Constructor(classToken);
        hasUserDeclaredConstructor = false;
    }

    public String getName() {return classToken.lexeme;}
    public int getLine() {return classToken.lineNumber;}
    public boolean isConcreteClass(){return true;}
    public HashMap<String, Attribute> getAttributeHashMap(){return attributeHashMap;}
    public HashMap<String, Method> getMethodHashMap() {return  methodHashMap;}

    public void hasCircularInheritance(HashMap<String, Token> inheritanceMap) throws SemanticException {
        if (classToken.lexeme.equals("Object")) // Si llegue a object no hay herencia circular
            return;
        if (inheritanceMap.get(extendsClassToken.lexeme) == null) { // Si no estoy en object, reviso si ya tengo en la lista recorrida la misma clase
            inheritanceMap.put(classToken.lexeme, extendsClassToken);
            symbolTable.getClass(extendsClassToken.lexeme).hasCircularInheritance(inheritanceMap);
        } else { // Si la tengo, reporto el error con la ultima linea que genere el problema
            Token lastToken = getLastInheritanceDeclaration(inheritanceMap, extendsClassToken);
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
        if (extendsClassToken != null)
            symbolTable.getClass(extendsClassToken.lexeme).consolidate();
        for (Token interfaceID : interfacesHashMap.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }

    private void checkInheritedAttributes() throws SemanticException {
        if (notObjectClass()) {
            for (Attribute attr : symbolTable.getClass(extendsClassToken.lexeme).getAttributeHashMap().values()) {
                if (attributeHashMap.get(attr.getName()) == null) {
                    if (attr.isPublic())
                        attributeHashMap.put(attr.getName(), attr); // Si es publico lo copio
                } else
                    throw new SemanticException("No se puede declarar un atributo con el mismo nombre que un atributo de clase padre", attr.getName(), attributeHashMap.get(attr.getName()).getLine());
            }
        }
    }

    private boolean notObjectClass() {
        return extendsClassToken != null;
    }

    private void checkInheritedMethods() throws SemanticException {
        checkRedefinedMethods();
        checkImplementedMethods();
    }

    private void checkRedefinedMethods() throws SemanticException {
        if (extendsClassToken != null) {
            for (Method method : symbolTable.getClass(extendsClassToken.lexeme).getMethodHashMap().values()) {
                if (methodHashMap.get(method.getName()) == null) {  // Si no redefine el metodo, se agrega
                    methodHashMap.put(method.getName(), method);
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName()))) {   // Si se redefine pero con distintos parametros o tipo de retorno, error semantico
                    throw new SemanticException("El método redefinido "+method.getName()+" tiene distinto retorno/parámetros", method.getName(), methodHashMap.get(method.getName()).getLine());
                }
            }
        }
    }

    private void checkImplementedMethods() throws SemanticException {
        for (Token intface : interfacesHashMap.values()){
            for (Method method : symbolTable.getClass(intface.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) == null) {          // Si no implementa un metodo, error
                    throw new SemanticException("Falta implementar el metodo "+method.getName()+" de la interface "+intface.lexeme, method.getName(), method.getLine());
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName()))) {   // Si lo implementa pero con distintos parametros, error
                    throw new SemanticException("El metodo "+method.getName()+" de la interface "+intface.lexeme+" está mal implementado", method.getName(), methodHashMap.get(method.getName()).getLine());
                }
            }
        }
    }

    private void checkInheritance() throws SemanticException {
        if (extendsClassToken != null && !symbolTable.classExists(extendsClassToken.lexeme))
            throw new SemanticException("No se puede extender a la clase "+ extendsClassToken.lexeme+", no existe", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        hasCircularInheritance(new HashMap<>());
    }

    private void checkInterfaces() throws SemanticException {
        for (Token impInterface : interfacesHashMap.values()) {
            if ((impInterface.lexeme).equals(classToken.lexeme))
                throw new SemanticException("La clase "+ extendsClassToken.lexeme+" no se puede implementar como interface a si misma", extendsClassToken.lexeme, extendsClassToken.lineNumber);
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

    public void setAncestorClass(Token classToken){
        this.extendsClassToken = classToken;}

    public void addMultipleInheritence(Token interfaceToken) throws SemanticException {
        if (interfacesHashMap.get(interfaceToken.lexeme) != null) {
            throw new SemanticException("No se puede implementar dos veces la interfaz "+ interfaceToken.lexeme, interfaceToken.lexeme, interfaceToken.lineNumber);
        } else interfacesHashMap.put(interfaceToken.lexeme, interfaceToken);
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
        if (hasUserDeclaredConstructor) {
            throw new SemanticException("No se puede declarar mas de un constructor",constructor.getName(), constructor.getLine());
        } else {
            this.constructor = constructor;
            hasUserDeclaredConstructor = true;
        }
    }
}
