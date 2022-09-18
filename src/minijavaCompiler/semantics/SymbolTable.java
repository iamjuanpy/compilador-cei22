package minijavaCompiler.semantics;

import minijavaCompiler.semantics.entities.ClassEntity;

import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, ClassEntity> claseHashMap;

    public SymbolTable(){
        claseHashMap = new HashMap<>();
    }

    public void checkDeclarations() throws SemanticException{
        claseHashMap.forEach((name,c) -> c.isWellDeclared());       // Paso 1: esta bien declarado
        claseHashMap.forEach((name,c) -> c.consolidate());          // Paso 2: consolidar clases/interfaces
    }

}
