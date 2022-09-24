package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;

import static minijavaCompiler.Main.symbolTable;

public class Interface implements ClassEntry {

    private Token interfaceToken;
    private HashMap<String, Method> methodHashMap;
    private HashMap<String, Token> interfacesHashMap;
    private boolean consolidated;

    public Interface(Token token){
        this.interfaceToken = token;
        methodHashMap = new HashMap<>();
        interfacesHashMap = new HashMap<>();
    }

    public String getName() {
        return interfaceToken.lexeme;
    }
    public int getLine() {
        return interfaceToken.lineNumber;
    }
    public HashMap<String, Attribute> getAttributeHashMap() {return null;}   // No llega
    public HashMap<String, Method> getMethodHashMap() {return  methodHashMap;}
    public boolean isConcreteClass() { return false;}

    // Chequeo declaraciones 

    public void correctlyDeclared() throws SemanticException {
        checkInheritance();
        checkMethods();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            consolidateAncestors();
            copyInheritedMethods();
            consolidated = true;
        }
    }

    //Chequeo correctamente declarado

    private void checkInheritance() throws SemanticException {
        for (Token extInterface : interfacesHashMap.values()) {
            if (!symbolTable.classExists(extInterface.lexeme))
                throw new SemanticException("No se puede extender la interface "+extInterface.lexeme+", no existe", extInterface.lexeme, extInterface.lineNumber);
            else if (symbolTable.getClass(extInterface.lexeme).isConcreteClass())
                throw new SemanticException("No se puede extender "+extInterface.lexeme+", es una clase concreta", extInterface.lexeme, extInterface.lineNumber);
        }
        hasCircularInheritance(new HashMap<>());
    }

    public void hasCircularInheritance(HashMap<String, Token> inheritanceMap) throws SemanticException {
        for (Token extendsInterface : interfacesHashMap.values()) { // Chequeo arbol de herencia de a un implement
            if (inheritanceMap.get(extendsInterface.lexeme) == null) { // Reviso si ya tengo en la lista recorrida la misma interface
                inheritanceMap.put(interfaceToken.lexeme, extendsInterface);
                symbolTable.getClass(extendsInterface.lexeme).hasCircularInheritance(inheritanceMap);
            } else { // Si la tengo, reporto el error con la ultima linea que genere el problema
                Token lastToken = getLastInheritanceDeclaration(inheritanceMap, extendsInterface);
                throw new SemanticException("No puede haber herencia circular", lastToken.lexeme, lastToken.lineNumber);
            }
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

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
    }

    // Consolidacion

    private void consolidateAncestors() throws SemanticException {
        for (Token interfaceID : interfacesHashMap.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }


    private void copyInheritedMethods() throws SemanticException {
        for (Token interfaceID : interfacesHashMap.values())
            for (Method method : symbolTable.getClass(interfaceID.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) != null) {
                    if (!method.hasSameSignature(methodHashMap.get(method.getName())))
                        throw new SemanticException("No se puede extender una interface teniendo un metodo redefinido con distintos parametros/retorno", method.getName(), getLastDeclaredMethod(method, methodHashMap.get(method.getName())).getLine());
                } else methodHashMap.put(method.getName(), method);
            }
    }

    private Method getLastDeclaredMethod(Method method1, Method method2) {
        if (method1.getLine() >= method2.getLine()) // Puedo estar extendiendo dos interfaces, elijo la ultima declarada para mostrar el error
            return method1;
        else return method2;
    }

    // Setters

    public void setAncestorClass(Token classToken) {} // NO LLEGA

    public void addMultipleInheritence(Token interfaceToken) throws SemanticException {
        if (interfacesHashMap.get(interfaceToken.lexeme) == null)
            interfacesHashMap.put(interfaceToken.lexeme, interfaceToken);
        else throw new SemanticException(interfaceToken.lexeme, interfaceToken.lineNumber);
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
