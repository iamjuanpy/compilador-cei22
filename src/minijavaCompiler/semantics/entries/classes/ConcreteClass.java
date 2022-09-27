package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.classID;

public class ConcreteClass implements ClassEntry {

    private Token classToken;
    private HashMap<String, Attribute> attributeHashMap;
//    private Constructor constructor;
    private List<Constructor> constructorsList;
    private HashMap<String, Method> methodHashMap;
    private Token extendsClassToken;
    private HashMap<String,Token> interfacesHashMap;
    private boolean consolidated;

    public ConcreteClass(Token token){
        this.classToken = token;
        attributeHashMap = new HashMap<>();
        constructorsList = new ArrayList<>();
        methodHashMap = new HashMap<>();
        interfacesHashMap = new HashMap<>();
        extendsClassToken = new Token(classID, "Object", token.lineNumber); // default: idClase extends Object {}
        consolidated = false;
    }

    public String getName() {return classToken.lexeme;}
    public int getLine() {return classToken.lineNumber;}
    public boolean isConcreteClass(){return true;}
    public HashMap<String, Attribute> getAttributeHashMap(){return attributeHashMap;}
    public HashMap<String, Method> getMethodHashMap() {return  methodHashMap;}
    private boolean notObjectClass() {
        return extendsClassToken != null;
    }

    // Chequeo declaraciones

    public void correctlyDeclared() throws SemanticException {
        if (notObjectClass())
            checkInheritance();
        checkInterfaces();
        checkAttributes();
        checkMethods();
        checkConstructor();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            if (notObjectClass()) {
                consolidateAncestors();
                consolidateMethods();
                copyInheritedAttributes();
            }
            consolidated = true;
        }
    }

    // Chequeo correctamente declarado

    private void checkInheritance() throws SemanticException {
        if (classExtendsItself())
            throw new SemanticException("La clase " + extendsClassToken.lexeme + " no se puede extender a si misma", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        else if (classExtendsNonExistent())
            throw new SemanticException("No se puede extender a la clase " + extendsClassToken.lexeme + ", no existe", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        else if (classExtendsAnInterface())
            throw new SemanticException("Una clase concreta no puede extender una interface ", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        checkCircularInheritance(new HashMap<>());
    }

    private boolean classExtendsItself() {return (extendsClassToken.lexeme).equals(classToken.lexeme);}

    private boolean classExtendsNonExistent() {return !symbolTable.classExists(extendsClassToken.lexeme);}

    private boolean classExtendsAnInterface() {return !symbolTable.getClass(extendsClassToken.lexeme).isConcreteClass();}

    public void checkCircularInheritance(HashMap<String, Token> inheritanceMap) throws SemanticException {
        if (classToken.lexeme.equals("Object"))                                                                     // Si llegue a object no hay herencia circular
            return;
        else if (inheritanceMap.get(extendsClassToken.lexeme) == null) {                                            // Si no estoy en object, reviso si ya tengo en la lista recorrida la misma clase
            inheritanceMap.put(classToken.lexeme, extendsClassToken);
            symbolTable.getClass(extendsClassToken.lexeme).checkCircularInheritance(inheritanceMap);
        } else {                                                                                                    // Si la tengo, reporto el error con la ultima linea que genere el problema
            Token lastToken = getLastInheritanceDeclaration(inheritanceMap, extendsClassToken);
            throw new SemanticException("No puede haber herencia circular en una clase", lastToken.lexeme, lastToken.lineNumber);
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

    private void checkInterfaces() throws SemanticException {
        for (Token impInterface : interfacesHashMap.values()) {
            if (classImplementsItself(impInterface))
                throw new SemanticException("La clase "+impInterface.lexeme+" no se puede implementar como interface a si misma", impInterface.lexeme, impInterface.lineNumber);
            else if (classImplementsNonExistent(impInterface))
                throw new SemanticException("No se puede implementar la interface "+impInterface.lexeme+", no existe", impInterface.lexeme, impInterface.lineNumber);
            else if (classImplementsConcreteClass(impInterface))
                throw new SemanticException("No se puede implementar "+impInterface.lexeme+", es una clase concreta", impInterface.lexeme, impInterface.lineNumber);
        }
    }

    private boolean classImplementsItself(Token interfaceToken) {return (interfaceToken.lexeme).equals(classToken.lexeme);}

    private boolean classImplementsNonExistent(Token interfaceToken) {return !symbolTable.classExists(interfaceToken.lexeme);}

    private boolean classImplementsConcreteClass(Token interfaceToken) {return symbolTable.getClass(interfaceToken.lexeme).isConcreteClass();}

    private void checkAttributes() throws SemanticException {
        for (Attribute attribute : attributeHashMap.values())
            attribute.correctlyDeclared();
    }

    private void checkConstructor() throws SemanticException {
//        if (constructor != null)
//            constructor.correctlyDeclared();
//        else createDefaultConstructor();
        if (constructorsList.size() != 0)
            for (Constructor c : constructorsList)
                c.correctlyDeclared();
        else {
            constructorsList.add(new Constructor(classToken));
        }
    }

//    private void createDefaultConstructor() {constructor = new Constructor(classToken);}

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
    }

    // Consolidacion

    private void consolidateAncestors() throws SemanticException {
        symbolTable.getClass(extendsClassToken.lexeme).consolidate();
        for (Token interfaceID : interfacesHashMap.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }

    private void copyInheritedAttributes() throws SemanticException {
        for (Attribute attr : symbolTable.getClass(extendsClassToken.lexeme).getAttributeHashMap().values()) {
            if (attributeHashMap.get(attr.getName()) == null) {
                attributeHashMap.put(attr.getName(), attr);
            } else throw new SemanticException("Una clase no puede declarar un atributo con el mismo nombre que otro en su línea de herencia", attr.getName(), attributeHashMap.get(attr.getName()).getLine());
        }
    }

    private void consolidateMethods() throws SemanticException {
        copyNotRedefinedMethods();
        checkImplementedMethods();
    }

    private void copyNotRedefinedMethods() throws SemanticException {
        if (notObjectClass()) {
            for (Method method : symbolTable.getClass(extendsClassToken.lexeme).getMethodHashMap().values()) {
                if (methodHashMap.get(method.getName()) == null) {
                    methodHashMap.put(method.getName(), method);
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName()))) {
                    throw new SemanticException("El método heredado "+method.getName()+" está mal redefinido", method.getName(), methodHashMap.get(method.getName()).getLine());
                }
            }
        }
    }

    private void checkImplementedMethods() throws SemanticException {
        for (Token intface : interfacesHashMap.values()){
            for (Method method : symbolTable.getClass(intface.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) == null) {
                    throw new SemanticException("Falta implementar el metodo "+method.getName()+" de la interface "+intface.lexeme, intface.lexeme, intface.lineNumber);
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName()))) {
                    throw new SemanticException("El metodo "+method.getName()+" de la interface "+intface.lexeme+" está mal implementado", intface.lexeme, intface.lineNumber);
                }
            }
        }
    }

    // Setters

    public void setAncestorClass(Token classToken){this.extendsClassToken = classToken;}

    public void addMultipleInheritence(Token interfaceToken) throws SemanticException {
        if (interfacesHashMap.get(interfaceToken.lexeme) != null) {
            throw new SemanticException("Una clase no puede implementar dos veces una interface ("+ interfaceToken.lexeme+")", interfaceToken.lexeme, interfaceToken.lineNumber);
        } else interfacesHashMap.put(interfaceToken.lexeme, interfaceToken);
    }

    public void addMethod(Method method) throws SemanticException {
        if (alreadyHasMethodWithName(method.getName())) {
            throw new SemanticException("Una clase no puede declarar mas de un método con el mismo nombre ("+method.getName()+")", method.getName(), method.getLine());
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
            throw new SemanticException("Una clase no puede tener mas de un atributo con el mismo nombre ("+attribute.getName()+")", attribute.getName(), attribute.getLine());
        } else attributeHashMap.put(attribute.getName(), attribute);
    }

    private boolean alreadyHasAttributeWithName(String name) {return attributeHashMap.get(name) != null;}

    public void addConstructor(Constructor constructor) throws SemanticException { // Comentar bloque del metodo para probar MULTIPLES constructores solo en sintactico
//        if (!constructor.getName().equals(classToken.lexeme))
//            throw new SemanticException("Constructor mal declarado", constructor.getName(),constructor.getLine());
//        else if (this.constructor != null) {
//            throw new SemanticException("Una clase no puede declarar mas de un constructor",constructor.getName(), constructor.getLine());
//        } else {
//            this.constructor = constructor;
        if (!constructor.getName().equals(classToken.lexeme))
            throw new SemanticException("Constructor mal declarado", constructor.getName(),constructor.getLine());
        else {
            for (Constructor c : constructorsList) {
                if (c.getParametersList().size() == constructor.getParametersList().size())
                    if (constructor.hasSameSignature(c))
                        throw new SemanticException("No se puede declarar dos constructores con los mismos parametros", constructor.getName(), constructor.getLine());
            }
            constructorsList.add(constructor);
        }
    }
}
