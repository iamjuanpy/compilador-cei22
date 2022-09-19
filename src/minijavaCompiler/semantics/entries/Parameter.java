package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.entries.types.Type;

public class Parameter {

    private Token idToken;
    private Type parameterType;
    private String parameterName;

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }
}
