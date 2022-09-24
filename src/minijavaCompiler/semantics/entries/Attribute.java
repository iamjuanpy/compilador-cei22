package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.entries.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class Attribute {

    private ClassEntry classDeclared;
    private Token idToken;
    private Type attrType;
    private boolean visibility;

    public Attribute(boolean visibility, Type type, Token id) {
        this.idToken = id;
        this.attrType = type;
        this.visibility = visibility;
        this.classDeclared = symbolTable.currentClass;
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }

    public boolean isPublic(){
        return visibility;
    }

    public void correctlyDeclared() throws SemanticException {
        if (!attrType.isPrimitive() && !symbolTable.classExists(attrType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un atributo de tipo "+attrType.getTypeName()+", la clase no existe", attrType.getTypeName(), attrType.getLine());
    }
}
