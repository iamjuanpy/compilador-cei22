package minijavaCompiler.semantics;

import minijavaCompiler.semantics.entries.*;

import java.util.HashMap;

public class SymbolTable {

    public ClassEntry currentClass;
    public Unit currentUnit;
    private HashMap<String, ClassEntry> classesHashMap;

    public SymbolTable(){
        classesHashMap = new HashMap<>();
    }

    public void checkDeclarations() throws SemanticException{
        classesHashMap.forEach((name, c) -> c.isWellDeclared());       // Paso 1: esta bien declarado
        classesHashMap.forEach((name, c) -> c.consolidate());          // Paso 2: consolidar clases/interfaces
    }

    public void setCurrentClass(ClassEntry classEntry) throws SemanticException {
        if (classesHashMap.get(classEntry.getName()) != null) {
            throw new SemanticException(classEntry.getName(), classEntry.getLine());
        } else currentClass = classEntry;
    }

    public void saveCurrentClass(){
        classesHashMap.put(currentClass.getName(), currentClass);
    }

    // MOMENTANEAMENTE PERMITIENDO 1 CONSTRUCTOR / 1 METODO POR NOMBRE

}
