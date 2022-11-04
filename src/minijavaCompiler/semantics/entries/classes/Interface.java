package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static minijavaCompiler.Main.symbolTable;

public class Interface implements ClassEntry {

    private Token interfaceToken;
    private HashMap<String, Method> methodHashMap;
    private HashMap<String, Token> interfaceExtendsHashMap;
    private boolean consolidated;

    private boolean offsetsSet;
    private int lastMethodOffset;

    public Interface(Token token){
        this.interfaceToken = token;
        methodHashMap = new HashMap<>();
        interfaceExtendsHashMap = new HashMap<>();
        consolidated = false;
    }

    public String getName() { return interfaceToken.lexeme;}
    public int getLine() { return interfaceToken.lineNumber;}
    public HashMap<String, Attribute> getAttributeHashMap() { return null;}   // No llega
    public HashMap<String, Method> getMethodHashMap() { return methodHashMap;}
    public boolean isConcreteClass() { return false;}

    public boolean isAttribute(String identifier) {return false;}
    public Attribute getAtrribute(String identifier) { return null;} // NO LLEGA

    public boolean isMethod(String identifier) {return methodHashMap.get(identifier) != null;}
    public Method getMethod(String identifier) {return methodHashMap.get(identifier);}

    public Constructor getConstructor() {return null;} // no llega

    public Set<String> getInheritanceSet() {
        HashSet<String> inheritanceSet = new HashSet<>();
        inheritanceSet.add(interfaceToken.lexeme);
        for (String intToken : interfaceExtendsHashMap.keySet())
            inheritanceSet.addAll(symbolTable.getClass(intToken).getInheritanceSet());
        return inheritanceSet;
    }

    public int getLastAttributeOffset() {return 0;}
    public int getLastMethodOffset() {return 0;}

    // Chequeo declaraciones

    public void correctlyDeclared() throws SemanticException {
        checkInheritanceDeclaration();
        checkMethods();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            checkInheritanceCircularity();
            consolidateAncestors();
            copyInheritedMethods();
            consolidated = true;
        }
    }

    // Chequeo sentencias, no llega
    public void checkSentences(){}

    //Chequeo correctamente declarado

    private void checkInheritanceDeclaration() throws SemanticException {
        for (Token extendsToken : interfaceExtendsHashMap.values()) {
            if (interfaceExtendsItself(extendsToken))
                throw new SemanticException("La interface "+extendsToken.lexeme+" no se puede extender a si misma", extendsToken.lexeme, extendsToken.lineNumber);
            else if (interfaceExtendsNonExistent(extendsToken))
                throw new SemanticException("No se puede extender la interface "+extendsToken.lexeme+", no existe", extendsToken.lexeme, extendsToken.lineNumber);
            else if (interfaceExtendsConcreteClass(extendsToken))
                throw new SemanticException("No se puede extender "+extendsToken.lexeme+", es una clase concreta", extendsToken.lexeme, extendsToken.lineNumber);
        }
    }

    private boolean interfaceExtendsItself(Token extInterface) {return interfaceToken.lexeme.equals(extInterface.lexeme);}

    private boolean interfaceExtendsNonExistent(Token extInterface) {return !symbolTable.classExists(extInterface.lexeme);}

    private boolean interfaceExtendsConcreteClass(Token extInterface) {return symbolTable.getClass(extInterface.lexeme).isConcreteClass();}

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
    }

    // Consolidacion

    private void checkInheritanceCircularity() throws SemanticException { checkInheritanceCircularity(new HashMap<>());}

    public void checkInheritanceCircularity(HashMap<String, Token> inheritanceMap) throws SemanticException {
        for (Token interfaceExtend : interfaceExtendsHashMap.values()) { // Chequeo arbol de herencia de a un extend
            if (inheritanceMap.get(interfaceExtend.lexeme) == null) {                                          // Reviso si ya tengo en la lista recorrida la misma interface
                inheritanceMap.put(interfaceToken.lexeme, interfaceExtend);
                symbolTable.getClass(interfaceExtend.lexeme).checkInheritanceCircularity(inheritanceMap);
            } else {                                                                                            // Si la tengo, reporto el error con la ultima linea que genere el problema
                Token lastToken = getLastInheritanceDeclaration(inheritanceMap, interfaceExtend);
                throw new SemanticException("No puede haber herencia circular en interfaces", lastToken.lexeme, lastToken.lineNumber);
            }
            inheritanceMap.remove(interfaceExtend.lexeme);
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

    private void consolidateAncestors() throws SemanticException {
        for (Token interfaceExtend : interfaceExtendsHashMap.values())
            symbolTable.getClass(interfaceExtend.lexeme).consolidate();
    }


    private void copyInheritedMethods() throws SemanticException {
        for (Token interfaceExtend : interfaceExtendsHashMap.values())
            for (Method method : symbolTable.getClass(interfaceExtend.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) != null) {
                    if (!method.hasSameSignature(methodHashMap.get(method.getName())))
                        throw new SemanticException("No se puede extender una interface teniendo un metodo redefinido con distintos parametros/retorno", interfaceExtend.lexeme, interfaceExtend.lineNumber);
                } else methodHashMap.put(method.getName(), method);
            }
    }

    // Generación de código

    public void generateCode(){ // No tiene codigo
    }

    public void setOffsets() { // TODO Puedo settear offsets o como busco los metodos?
        if (!offsetsSet){
            
        }
        offsetsSet = true;
    }

    public String getVTableLabel() {return null;} // NO LLEGA

    // Setters

    public void setAncestorClass(Token classToken) {} // NO LLEGA

    public void addMultipleInheritence(Token interfaceToken) throws SemanticException {
        if (interfaceExtendsHashMap.get(interfaceToken.lexeme) == null)
            interfaceExtendsHashMap.put(interfaceToken.lexeme, interfaceToken);
        else throw new SemanticException("Una interface no puede extender dos veces la misma interface ("+interfaceToken.lexeme+")",interfaceToken.lexeme, interfaceToken.lineNumber);
    }

    public void addAttribute(Attribute attribute) throws SemanticException {} // NO LLEGA
    public void addConstructor(Constructor constructor) throws SemanticException {} // NO LLEGA

    public void addMethod(Method method) throws SemanticException {
        if (method.isStatic())
            throw new SemanticException("Una interface no puede declarar métodos estáticos ("+method.getName()+")", method.getName(), method.getLine());
        else if (methodHashMap.get(method.getName()) == null)
            methodHashMap.put(method.getName(), method);
        else throw new SemanticException("Una interface no puede declarar mas de un método con el mismo nombre ("+method.getName()+")", method.getName(), method.getLine());
    }

}
