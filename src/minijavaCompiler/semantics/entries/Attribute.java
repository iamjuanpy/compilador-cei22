package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.entries.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class Attribute {

    private Token idToken;
    private Type attrType;
    private boolean visibility;

    public Attribute(boolean visibility, Type type, Token id) {
        this.idToken = id;
        this.attrType = type;
        this.visibility = visibility;
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }

}
