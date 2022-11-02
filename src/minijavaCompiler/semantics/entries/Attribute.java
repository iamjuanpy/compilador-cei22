package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class Attribute implements Variable {

    private ClassEntry classDeclared;
    private Token attributeToken;
    private Type attributeType;
    private boolean isPublic;

    private int offset;

    public Attribute(boolean isPublic, Type type, Token id) {
        this.attributeToken = id;
        this.attributeType = type;
        this.isPublic = isPublic;
        this.classDeclared = symbolTable.currentClass;
    }

    public String getName() {
        return attributeToken.lexeme;
    }
    public int getLine() {
        return attributeToken.lineNumber;
    }
    public ClassEntry getClassDeclared() {return classDeclared;}
    public Type getType() {return attributeType;}
    public boolean isPublic(){
        return isPublic;
    }

    public void correctlyDeclared() throws SemanticException {
        if (!attributeType.isPrimitive() && !symbolTable.classExists(attributeType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un atributo de tipo "+ attributeType.getTypeName()+", la clase no existe", attributeType.getTypeName(), attributeType.getLine());
    }

    public void setOffset(int offset) {this.offset = offset;}
    public int getOffset(){return offset;}

}
