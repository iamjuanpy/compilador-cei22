package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

import java.util.HashMap;
import java.util.List;

public class Constructor implements Unit {

    private Token idToken;
    private HashMap<String, Parameter> parameterHashMap;
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

    public void addParameter(Parameter parameter) throws SemanticException {

    }
}
