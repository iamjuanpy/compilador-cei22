package minijavaCompiler.semantics.entries.classes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.*;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.classID;

public class ConcreteClass implements ClassEntry {

    private Token classToken;
    private HashMap<String, Attribute> attributeHashMap;
    private Constructor constructor;
    private HashMap<String, Method> methodHashMap;
    private Token extendsClassToken;
    private HashMap<String,Token> interfacesHashMap;
    private boolean consolidated;

    private boolean methodsOffsetSet;
    private boolean attributesOffsetSet;

    private int lastAttributeOffset;

    public ConcreteClass(Token token){
        this.classToken = token;
        attributeHashMap = new HashMap<>();
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
    private boolean notObjectClass() {return extendsClassToken != null;}

    public boolean isAttribute(String identifier) {return attributeHashMap.get(identifier) != null;}
    public Attribute getAttribute(String identifier) { return attributeHashMap.get(identifier);}

    public boolean isMethod(String identifier) {return methodHashMap.get(identifier) != null;}
    public Method getMethod(String identifier) {return methodHashMap.get(identifier);}

    public Constructor getConstructor() {return constructor;}

    public Set<String> getInheritanceSet() {
        HashSet<String> inheritanceSet = new HashSet<>();
        inheritanceSet.add(classToken.lexeme);
        if (!classToken.lexeme.equals("Object")){
            inheritanceSet.addAll(symbolTable.getClass(extendsClassToken.lexeme).getInheritanceSet());
            for (String intToken : interfacesHashMap.keySet())
                inheritanceSet.addAll(symbolTable.getClass(intToken).getInheritanceSet());
        }
        return inheritanceSet;
    }

    public String getVTableLabel() {return "VT_"+classToken.lexeme;}

    // Chequeo declaraciones

    public void correctlyDeclared() throws SemanticException {
        if (notObjectClass())
            checkInheritanceDeclaration();
        checkInterfaces();
        checkAttributes();
        checkMethods();
        checkConstructor();
    }

    public void consolidate() throws SemanticException {
        if (!consolidated){
            if (notObjectClass()) {
                checkInheritanceCircularity();
                consolidateAncestors();
                consolidateMethods();
                copyInheritedAttributes();
            }
            consolidated = true;
        }
    }

    // Chequeo sentencias

    public void checkSentences() throws SemanticException {
        constructor.checkSentences();
        for (Method m : methodHashMap.values()) {
            if (m.getClassDeclared() == this) {
                m.checkSentences();
            }
        }
    }

    // Chequeo correctamente declarado

    private void checkInheritanceDeclaration() throws SemanticException {
        if (classExtendsItself())
            throw new SemanticException("La clase " + extendsClassToken.lexeme + " no se puede extender a si misma", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        else if (classExtendsNonExistent())
            throw new SemanticException("No se puede extender a la clase " + extendsClassToken.lexeme + ", no existe", extendsClassToken.lexeme, extendsClassToken.lineNumber);
        else if (classExtendsAnInterface())
            throw new SemanticException("Una clase concreta no puede extender una interface ", extendsClassToken.lexeme, extendsClassToken.lineNumber);
    }

    private boolean classExtendsItself() {return (extendsClassToken.lexeme).equals(classToken.lexeme);}

    private boolean classExtendsNonExistent() {return !symbolTable.classExists(extendsClassToken.lexeme);}

    private boolean classExtendsAnInterface() {return !symbolTable.getClass(extendsClassToken.lexeme).isConcreteClass();}

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
        if (constructor != null)
            constructor.correctlyDeclared();
        else createDefaultConstructor();
    }

    private void createDefaultConstructor() {
        constructor = new Constructor(classToken);
        constructor.addBlock(new NodeBlock());
    }

    private void checkMethods() throws SemanticException {
        for (Method method : methodHashMap.values())
            method.correctlyDeclared();
    }

    // Consolidacion

    private void checkInheritanceCircularity() throws SemanticException { checkInheritanceCircularity(new HashMap<>());}

    public void checkInheritanceCircularity(HashMap<String, Token> inheritanceMap) throws SemanticException {
        if (classToken.lexeme.equals("Object"))                                                                     // Si llegue a object no hay herencia circular
            return;
        else {
            if (inheritanceMap.get(extendsClassToken.lexeme) == null) {                                            // Si no estoy en object, reviso si ya tengo en la lista recorrida la misma clase
                inheritanceMap.put(classToken.lexeme, extendsClassToken);
                symbolTable.getClass(extendsClassToken.lexeme).checkInheritanceCircularity(inheritanceMap);
            } else {                                                                                                    // Si la tengo, reporto el error con la ultima linea que genere el problema
                Token lastToken = getLastInheritanceDeclaration(inheritanceMap, extendsClassToken);
                throw new SemanticException("No puede haber herencia circular en clases", lastToken.lexeme, lastToken.lineNumber);
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

    private void consolidateAncestors() throws SemanticException {
        symbolTable.getClass(extendsClassToken.lexeme).consolidate();
        for (Token interfaceID : interfacesHashMap.values())
            symbolTable.getClass(interfaceID.lexeme).consolidate();
    }

    private void copyInheritedAttributes() throws SemanticException {
        for (Attribute attr : symbolTable.getClass(extendsClassToken.lexeme).getAttributeHashMap().values()) {
            if (attributeHashMap.get(attr.getName()) == null) {
                attributeHashMap.put(attr.getName(), attr);
            } else throw new SemanticException("Una clase no puede declarar un atributo con el mismo nombre que otro en su l??nea de herencia", attr.getName(), attributeHashMap.get(attr.getName()).getLine());
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
                    method.addInheritedIn(this);
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName())))
                    throw new SemanticException("El m??todo heredado "+method.getName()+" est?? mal redefinido", method.getName(), methodHashMap.get(method.getName()).getLine());
                else {
                    methodHashMap.get(method.getName()).setRedefining(method);
                    method.addRedefinedBy(methodHashMap.get(method.getName()));
                }
            }
        }
    }

    private void checkImplementedMethods() throws SemanticException {
        for (Token intface : interfacesHashMap.values()){
            for (Method method : symbolTable.getClass(intface.lexeme).getMethodHashMap().values()){
                if (methodHashMap.get(method.getName()) == null) {
                    throw new SemanticException("Falta implementar el metodo "+method.getName()+" de la interface "+intface.lexeme, intface.lexeme, intface.lineNumber);
                } else if (!method.hasSameSignature(methodHashMap.get(method.getName())))
                    throw new SemanticException("El metodo "+method.getName()+" de la interface "+intface.lexeme+" est?? mal implementado", intface.lexeme, intface.lineNumber);
                method.addImplementation(methodHashMap.get(method.getName()));
            }
        }
    }

    // Generaci??n de c??digo

    public void generateCode(){
        generateVT();
        generateConstructorAndMethodsCode();
    }

    private void generateVT() {
        HashMap<Integer, String> methodsLabelByOffset = new HashMap<>();
        for (Method m : methodHashMap.values()) {
            if (!m.isStatic())
                methodsLabelByOffset.put(m.getOffset(), m.getLabel());
        }

        if (methodsLabelByOffset.size() != 0){
            symbolTable.ceiASM_instructionList.add(".data");
            String methodsLabels = "";
            for (int i = 0; i < getLastMethodOffset(); i++) {
                if (methodsLabelByOffset.get(i) != null)
                    methodsLabels += methodsLabelByOffset.get(i);
                else methodsLabels += "0";
                if (i != getLastMethodOffset()-1)
                    methodsLabels += ",";
            }
            symbolTable.ceiASM_instructionList.add("VT_"+classToken.lexeme+": DW "+methodsLabels+" ; Etiquetas de metodo de " + classToken.lexeme);
        } else {
            symbolTable.ceiASM_instructionList.add(".data");
            symbolTable.ceiASM_instructionList.add("VT_"+classToken.lexeme+": NOP ; Clase sin metodos dinamicos");
        }
        symbolTable.ceiASM_instructionList.add("");
    }

    private void generateConstructorAndMethodsCode() {
        constructor.generateCode();
        symbolTable.ceiASM_instructionList.add("");
        if (notDefaultClass()) // El codigo de los metodos de las clases default ya esta creado
            for (Method m : methodHashMap.values()) {
                if (m.getClassDeclared().equals(this)) { // Solo genera c??digo para los m??todos en su declaracion/redefinicion
                    m.generateCode();
                    symbolTable.ceiASM_instructionList.add("");
                }
            }
    }

    private boolean notDefaultClass(){return !(classToken.lexeme.equals("String") || classToken.lexeme.equals("Object") || classToken.lexeme.equals("System"));}

    // Offsets de metodos

    public void setMethodsOffsets() {
        if (!methodsOffsetSet){
            constructor.setParametersOffsets();

            int lastMethodOffset;
            if (notObjectClass()){
                ClassEntry fatherClass = symbolTable.getClass(extendsClassToken.lexeme);
                fatherClass.setMethodsOffsets();
                lastMethodOffset = fatherClass.getLastMethodOffset();
            } else lastMethodOffset = 0;

            for (Method method : methodHashMap.values()) {
                method.setParametersOffsets(); // Los offsets de los parametros
                if (!method.isStatic()) { // Si es estatico no pertenece a la VT
                    if (methodIsNotInherited(method)) {
                        if (methodIsARedefinition(method))
                            method.setOffset(symbolTable.getClass(extendsClassToken.lexeme).getMethod(method.getName()).getOffset());
                        else method.setOffset(lastMethodOffset++);
                    }
                }
            }

            methodsOffsetSet = true;
        }
    }

    private boolean methodIsNotInherited(Method method) {return method.getClassDeclared().equals(this);}
    private boolean methodIsARedefinition(Method method) {return symbolTable.getClass(extendsClassToken.lexeme).isMethod(method.getName());}

    public void fixConflictingMethodOffsets() {}

    public int getLastMethodOffset() {
        int maxOffset = -1;
        for (Method m : methodHashMap.values()) {
            if (!m.isStatic()) {
                if (m.getOffset() > maxOffset)
                    maxOffset = m.getOffset();
            }
        }
        return maxOffset + 1;
    }

    // Offsets de atributos

    public void setAttributesOffsets() {
        if (!attributesOffsetSet) {
            if (notObjectClass()) {
                ClassEntry fatherClass = symbolTable.getClass(extendsClassToken.lexeme);
                fatherClass.setAttributesOffsets();
                lastAttributeOffset = fatherClass.getLastAttributeOffset();
            } else lastAttributeOffset = 1; // Los atributos arranca en 1 en el CIR, la direccion a la VT esta en 0

            for (Attribute a : attributeHashMap.values())
                if (a.getClassDeclared().equals(this))
                    a.setOffset(lastAttributeOffset++);

            attributesOffsetSet = true;
        }
    }

    public int getLastAttributeOffset() {return lastAttributeOffset;}

    // Setters

    public void setAncestorClass(Token classToken){this.extendsClassToken = classToken;}

    public void addMultipleInheritance(Token interfaceToken) throws SemanticException {
        if (interfacesHashMap.get(interfaceToken.lexeme) != null) {
            throw new SemanticException("Una clase no puede implementar dos veces una interface ("+ interfaceToken.lexeme+")", interfaceToken.lexeme, interfaceToken.lineNumber);
        } else interfacesHashMap.put(interfaceToken.lexeme, interfaceToken);
    }

    public void addMethod(Method method) throws SemanticException {
        if (alreadyHasMethodWithName(method.getName())) {
            throw new SemanticException("Una clase no puede declarar mas de un m??todo con el mismo nombre ("+method.getName()+")", method.getName(), method.getLine());
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
        if (!constructor.getName().equals(classToken.lexeme))
            throw new SemanticException("Constructor mal declarado", constructor.getName(),constructor.getLine());
        else if (this.constructor != null) {
            throw new SemanticException("Una clase no puede declarar mas de un constructor",constructor.getName(), constructor.getLine());
        } else {
            this.constructor = constructor;
        }
    }
}
