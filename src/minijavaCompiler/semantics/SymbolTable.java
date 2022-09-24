package minijavaCompiler.semantics;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.entries.Unit;

import java.util.HashMap;

public class SymbolTable {

    public ClassEntry currentClass;
    public Unit currentUnit;
    public Unit mainMethod;
    public Token eofToken;

    private HashMap<String, ClassEntry> classesHashMap;

    public SymbolTable() {
        classesHashMap = new HashMap<>();
    }

    public void checkDeclarations() throws SemanticException {
        for (ClassEntry c : classesHashMap.values()) c.correctlyDeclared();     // Paso 1: esta bien declarado
        for (ClassEntry c : classesHashMap.values()) c.consolidate();           // Paso 2: consolidar clases/interfaces
        if (mainMethod == null)                                                 // Paso 3: Ver que exista un metodo main
            throw new SemanticException("Ninguna clase tiene metodo main", eofToken.lexeme, eofToken.lineNumber);
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

    public void saveCurrentClass() {
        classesHashMap.put(currentClass.getName(), currentClass);
    }

}
