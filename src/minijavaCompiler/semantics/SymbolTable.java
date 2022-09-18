package minijavaCompiler.semantics;

import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.ClassEntry;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;

import java.util.HashMap;

public class SymbolTable {

    private ClassEntry currentClass;
    private Attribute lastAddedAttribute;
    private Constructor currentConstructor;
    private Method currentMethod;

    private HashMap<String, ClassEntry> classesHashMap;

    public SymbolTable(){
        classesHashMap = new HashMap<>();
    }

    public void checkDeclarations() throws SemanticException{
        classesHashMap.forEach((name, c) -> c.isWellDeclared());       // Paso 1: esta bien declarado
        classesHashMap.forEach((name, c) -> c.consolidate());          // Paso 2: consolidar clases/interfaces
    }

    public void setCurrentClass(ClassEntry classEntry) throws SemanticException {
        if (classesHashMap.get(classEntry.getName()) == null) {
            currentClass = classEntry;
        } else throw new SemanticException(classEntry.getName(), classEntry.getLine());
    }
    public void setCurrentConstructor(Constructor constructorEntry){currentConstructor = constructorEntry;}
    public void setCurrentMethod(Method methodEntry){currentMethod = methodEntry;}

    public void addCurrentClass(){classesHashMap.put(currentClass.getName(), currentClass);}
    public void addAttribute(Attribute attr){
        currentClass.addAttribute(attr);
        lastAddedAttribute = attr;
    }
    public void addCurrentConstructor(){currentClass.addConstructor(currentConstructor);}
    public void addCurrentMethod(){currentClass.addMethod(currentMethod);}


}
