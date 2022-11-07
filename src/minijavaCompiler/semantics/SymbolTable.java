package minijavaCompiler.semantics;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.entries.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTable {

    public ClassEntry currentClass;
    public Unit currentUnit;
    public NodeBlock currentBlock;
    public Unit mainMethod;
    public Token eofToken;

    public HashMap<String, ClassEntry> classesHashMap;

    public List<String> ceiASM_instructionList;
    private int uniqueLabelNumber;

    public SymbolTable() {
        ceiASM_instructionList = new ArrayList<>(500);
        classesHashMap = new HashMap<>();
    }

    public void checkDeclarations() throws SemanticException {
        for (ClassEntry c : classesHashMap.values()) c.correctlyDeclared();     // Paso 1: esta bien declarado
        for (ClassEntry c : classesHashMap.values()) c.consolidate();           // Paso 2: consolidar clases/interfaces
        if (mainMethod == null)                                                 // Paso 3: Ver que exista un metodo main
            throw new SemanticException("No se encontró clase con metodo main", eofToken.lexeme, eofToken.lineNumber);
    }

    public void checkSentences() throws SemanticException{
        for (ClassEntry c : classesHashMap.values()) {
            if (c.isConcreteClass()) {
                c.checkSentences();
            }
        }
    }

    public void setOffsets() {
        for (ClassEntry c : classesHashMap.values()) c.setAttributesOffsets();  // Paso 1: offsets de atributos
        for (ClassEntry c : classesHashMap.values()) c.setMethodsOffsets(); // Paso 2: offsets de metodos de CLASES CONCRETAS
        for (ClassEntry c : classesHashMap.values()) c.fixConflictingMethodOffsets(); // Paso 3: corregir offsets de metodos a partir de INTERFACES
    }

    public void generateCode() {
        generateMainCall();
        generateHeapAllocCall();
        DefaultClasses.generateDefaultMethodsCode();
        for (ClassEntry c : classesHashMap.values()) c.generateCode();
    }

    private void generateMainCall() {
        ceiASM_instructionList.add(".code");
        ceiASM_instructionList.add("    PUSH simple_heap_init");
        ceiASM_instructionList.add("    CALL ; Inicializa heap");
        ceiASM_instructionList.add("    PUSH "+mainMethod.getLabel()+" ; Pushea label del main");
        ceiASM_instructionList.add("    CALL ; Llama a main");
        ceiASM_instructionList.add("    HALT ; Finaliza la ejecucion del programa");
        ceiASM_instructionList.add("");
    }

    private void generateHeapAllocCall() {
        ceiASM_instructionList.add("simple_heap_init:");
        ceiASM_instructionList.add("    RET 0 ; Init de heap");
        ceiASM_instructionList.add("");
        ceiASM_instructionList.add("simple_malloc:");
        ceiASM_instructionList.add("    LOADFP ; Init unidad");
        ceiASM_instructionList.add("    LOADSP");
        ceiASM_instructionList.add("    STOREFP ; Fin de init");
        ceiASM_instructionList.add("    LOADHL");
        ceiASM_instructionList.add("    DUP");
        ceiASM_instructionList.add("    PUSH 1 ; Principio de bloque sigue al final del heap");
        ceiASM_instructionList.add("    ADD");
        ceiASM_instructionList.add("    STORE 4 ; Puntero a principio de bloque");
        ceiASM_instructionList.add("    LOAD 3 ; Cantidad de celdas a alojar");
        ceiASM_instructionList.add("    ADD");
        ceiASM_instructionList.add("    STOREHL ; Actualiza heap size");
        ceiASM_instructionList.add("    STOREFP");
        ceiASM_instructionList.add("    RET 1 ; Retorna, liberando parametro");
        ceiASM_instructionList.add("");
    }

    public boolean classExists(String className){
        return classesHashMap.get(className) != null;
    }

    public ClassEntry getClass(String className){
        return classesHashMap.get(className);
    }

    public void setCurrentClass(ClassEntry classEntry) throws SemanticException {
        if (classesHashMap.get(classEntry.getName()) != null) {
            throw new SemanticException("No puede haber una clase/interface con nombre repetido, "+classEntry.getName(),classEntry.getName(), classEntry.getLine());
        } else currentClass = classEntry;
    }

    public void saveCurrentClass() {classesHashMap.put(currentClass.getName(), currentClass);}

    public String getUniqueLabel(){return "E"+(uniqueLabelNumber++);}

}
