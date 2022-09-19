package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;

import java.util.HashMap;
import java.util.List;

public class Constructor {

    private Token idToken;
    private List<Parameter> parameterList;

    public Constructor(Token constructorId) {
        idToken = constructorId;
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }
}
